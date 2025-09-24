package com.kodedu.service;

import com.kodedu.component.AlertHelper;
import com.kodedu.component.DiffEditor;
import com.kodedu.config.FileHistoryConfigBean;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.RevContent;
import com.kodedu.service.impl.DirectoryServiceImpl;
import com.kodedu.service.impl.ThreadServiceImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.*;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
public class GitFileService {

    private final DiffEditor diffEditor;

    private final Logger logger = LoggerFactory.getLogger(GitFileService.class);

    public final Map<Path, Git> map = new ConcurrentHashMap<>();

    private final ThreadServiceImpl threadService;
    private final DirectoryServiceImpl directoryService;
    private final FileHistoryConfigBean fileHistoryConfigBean;

    public GitFileService(DiffEditor diffEditor, ThreadServiceImpl threadService, DirectoryServiceImpl directoryService, FileHistoryConfigBean fileHistoryConfigBean) {
        this.diffEditor = diffEditor;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.fileHistoryConfigBean = fileHistoryConfigBean;
    }

    public synchronized void commitFileChanges(Path workingDirectory, Path currentFile) {
        Path gitPath = null;
        Path closestGitPath = null;
        boolean hasSubGit = false;
        try {
            gitPath = resolveGitPath(workingDirectory, currentFile);
            Git git = getGitByPath(gitPath);

            String pathPattern = getGitFilePattern(currentFile, gitPath);

            closestGitPath = getClosestGitPath(currentFile);
            hasSubGit = Objects.nonNull(closestGitPath) && !Objects.equals(closestGitPath, gitPath);

            if (hasSubGit) {
                moveSubGitTemporarily(closestGitPath, git, pathPattern);
            }

            DirCache dirCache = git.add()
                    .addFilepattern(pathPattern)
                    .call();
            Status status = git.status().call();
            if (status.hasUncommittedChanges()) {
                logger.warn("File pattern: {}", pathPattern);
                String commitMessage = getCommitMessage(gitPath, currentFile, git);
                if (Objects.nonNull(commitMessage)) {
                    logger.warn("Committing: {}", commitMessage);
                    RevCommit revCommit = git.commit().setMessage(commitMessage).call();
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (hasSubGit) {
                IOHelper.move(closestGitPath.resolve(".git2"), closestGitPath.resolve(".git"), ATOMIC_MOVE);
            }
        }

    }

    private void moveSubGitTemporarily(Path closestGitPath, Git git, String pathPattern) {
        try {
            IOHelper.move(closestGitPath.resolve(".git"), closestGitPath.resolve(".git2"), ATOMIC_MOVE);
            git.rm().setCached(true).addFilepattern(pathPattern).call();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public synchronized void showFileHistory(Path workingDirectory, Path currentFile) {
        Path gitPath = resolveGitPath(workingDirectory, currentFile);
        try {
            Git git = getGitByPath(gitPath);
            String pathPattern = getGitFilePattern(currentFile, gitPath);
            LogCommand logCommand = git.log();
            if (!pathPattern.isEmpty()) {
                logCommand.addPath(pathPattern);
            }
            Iterable<RevCommit> revCommits = logCommand.setMaxCount(50).call();

            List<RevContent> revList = StreamSupport
                    .stream(revCommits.spliterator(), false)
                    .map(r -> getRevContent(git, r))
                    .collect(Collectors.toList());

            Map<String, List<RevContent>> revMap = revList.stream()
                    .flatMap(rev -> rev.getAllRevPaths().stream()
                            .map(path -> Map.entry(path, rev)))
                    .collect(Collectors.groupingBy(Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())));


            diffEditor.updateContent(revList, revMap);
        } catch (NoHeadException noHeadException) {
            threadService.runActionLater(() -> {
                AlertHelper.showAlert("No history found");
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getGitFilePattern(Path currentFile, Path gitPath) {
        String path = gitPath.relativize(currentFile).toString();
        return path;
    }

    private Path resolveGitPath(Path workingDirectory, Path currentFile) {
        boolean containsPath = IOHelper.containsPath(workingDirectory, currentFile);
        Path gitPath = containsPath ? workingDirectory : currentFile.getParent();
        return gitPath;
    }

    private RevContent getRevContent(Git git, RevCommit commit) {
        RevContent revContent = new RevContent(commit);
        try {
            // Check if the commit has a parent
            boolean hasParent = commit.getParentCount() > 0;
            RevCommit parent = hasParent ? commit.getParent(0) : commit;
            try (ObjectReader reader = git.getRepository().newObjectReader()) {
                // Retrieve the commit and parent trees
                CanonicalTreeParser commitTreeIter = new CanonicalTreeParser();
                ObjectId commitTreeId = commit.getTree().getId();
                commitTreeIter.reset(reader, commitTreeId);

                CanonicalTreeParser parentTreeIter = new CanonicalTreeParser();
                ObjectId parentTreeId = parent.getTree().getId();
                parentTreeIter.reset(reader, parentTreeId);

                try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    diffFormatter.setRepository(git.getRepository());
                    // Scan for differences between commit and parent
                    List<DiffEntry> entries = diffFormatter.scan(parentTreeIter, commitTreeIter);
                    for (DiffEntry entry : entries) {
                        String newPath = entry.getNewPath();
                        // Retrieve content for each modified file
                        String content = getFileContent(git, newPath, commit);
                        revContent.addRevPath(newPath, content);
                    }
                }

                commitTreeIter.reset(reader, commitTreeId);

                // Check for newly added files (not present in parent commit)
                try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
                    treeWalk.addTree(commitTreeIter);
                    treeWalk.setRecursive(true);
                    while (treeWalk.next()) {
                        // Check if the path exists in the parent commit's tree
                        String newPath = treeWalk.getPathString();
//                        if (!isFileInTree(newPath, parentTreeIter)) {
                            String content = getFileContent(git, newPath, commit);
                            revContent.addInheritedPath(newPath, content);
//                        }
                    }
                }
            }
            // Return the RevContent object
            return revContent;
        } catch (IOException e) {
            // Handle any IO exceptions
            throw new RuntimeException(e);
        }
    }

    // Helper method to check if a file is in a tree
    private boolean isFileInTree(String path, CanonicalTreeParser treeParser) throws IOException {
        while (!treeParser.eof()) {
            if (path.equals(treeParser.getEntryPathString())) {
                return true;
            }
            treeParser.next();
        }
        return false;
    }


    private static String getFileContent(Git git, String path, RevCommit commit) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, commit.getTree());
        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = git.getRepository().open(objectId);
        String content = new String(loader.getBytes(), StandardCharsets.UTF_8);
        treeWalk.close();
        return content;
    }

    private Path getClosestGitPath(Path currentFile) {
        Path parent = currentFile.getParent();
        while (parent != null) {
            Path currentGitPath = parent.resolve(".git");
            if (Files.exists(currentGitPath)) {
                return currentGitPath.getParent();
            }
            parent = parent.getParent();
        }
        return null;
    }

    private boolean isNewFile(Repository repository, Path gitPath, Path currentFile) throws IOException {
        ObjectId lastCommitId = repository.resolve(Constants.HEAD);

        // if there's no commit, it's definitely a new file
        if (lastCommitId == null) {
            return true;
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            RevTree tree = commit.getTree();

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                String string = gitPath.relativize(currentFile).toString();
                treeWalk.setFilter(PathFilter.create(string));
                if (!treeWalk.next()) {
                    return true;
                }
            } finally {
                revWalk.dispose();
            }
        }
        return false;
    }

    private String getCommitMessage(Path gitPath, Path currentFile, Git git) throws IOException {

        boolean newFile = isNewFile(git.getRepository(), gitPath, currentFile);

        if (newFile) {
            String commitMessage = getCommitMessageSubstring(currentFile, IOHelper.readFile(currentFile));
            return commitMessage;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Repository repository = git.getRepository();
        AbstractTreeIterator oldTreeIterator = prepareTreeParser(repository, Constants.HEAD);
        DirCacheIterator newTreeIterator = new DirCacheIterator(repository.readDirCache());

        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(repository);
//            formatter.setAbbreviationLength(2);
//            formatter.setContext(0);
//            formatter.setDetectRenames(true);
            formatter.format(oldTreeIterator, newTreeIterator);
        }

        String diffOutput = out.toString("UTF-8"); // Getting full diff output as string
        IOHelper.close(out);

        if (diffOutput.isEmpty()) {
            return null;
        }

        String collected = Arrays.stream(diffOutput.split("\\r?\\n"))
                .filter(e -> !e.startsWith("+++"))
                .filter(e -> !e.startsWith("---"))
                .filter(e -> e.startsWith("+") || e.startsWith("-"))
                .sorted((a, b) -> {
                    if (a.startsWith("+") && b.startsWith("+")) {
                        return 0;
                    }
                    if (a.startsWith("-") && b.startsWith("-")) {
                        return 0;
                    }
                    return a.compareTo(b);
                })
                .map(e -> e.replaceFirst("\\+", ""))
                .map(e -> e.replaceFirst("-", ""))
                .collect(Collectors.joining(", "));

        String commitMessage = getCommitMessageSubstring(currentFile, collected);
        return commitMessage;
    }

    private static String getCommitMessageSubstring(Path currentFile, String collected) {
        String commitMessage;
        if (collected.length() > 75) { // If diffOutput has more than 30 characters
            commitMessage = collected.substring(0, 75); // Take first 30 characters
        } else if (collected.length() > 3) {
            commitMessage = collected; // Use full output as commit message
        } else {
            commitMessage = "Changes #" + currentFile.getFileName();

        }
        return commitMessage;
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        Ref head = repository.findRef(ref);
        if (Objects.isNull(head)) {
            return new EmptyTreeIterator();
        }
        ObjectId headObjectId = head.getObjectId();
        if (Objects.isNull(headObjectId)) {
            return new EmptyTreeIterator();
        }

        try (RevWalk walk = new RevWalk(repository)) {

            RevCommit commit = walk.parseCommit(headObjectId);
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    private Git getGitByPath(Path path) {
        return map.compute(path, this::getGitByPath);
    }

    private Git getGitByPath(Path path, Git git) {
        if (Objects.nonNull(git)) {
            return git;
        }

        try {

            String userHome = System.getProperty("user.home");
            Path fileHistoryRoot = IOHelper.getPath(userHome).resolve("AsciidocFX-FileHistory-Root");

            String globalFileHistoryRootDir = fileHistoryConfigBean.getGlobalFileHistoryRootDir();
            if (Objects.isNull(globalFileHistoryRootDir)) {
                String historyRootDir = fileHistoryRoot.toString();
                threadService.runActionLater(() -> {
                    fileHistoryConfigBean.setGlobalFileHistoryRootDir(historyRootDir);
                });
            } else {
                fileHistoryRoot = Paths.get(globalFileHistoryRootDir);
            }
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Path pathRelative = Paths.get(path.getRoot().relativize(path).toString());
            Path gitRoot = fileHistoryRoot.resolve(pathRelative);
            Files.createDirectories(gitRoot);
            checkAndCreateRepository(gitRoot);
            Repository repository = builder.setGitDir(gitRoot.resolve(".git").toFile())
                    .setWorkTree(path.toFile())
//                    .readEnvironment() // scan environment GIT_* variables
                    .build();
            git = new Git(repository);
//            git.reset()
//                    .setMode(ResetCommand.ResetType.HARD)
//                    .call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return git;
    }

    private void checkAndCreateRepository(Path directory) {

        try (FileRepository repository = new FileRepository(directory.resolve(".git").toFile())) {
            boolean isGitRepo = true;

            try (Git git = new Git(repository)) {
                try (RevWalk revWalk = new RevWalk(repository)) {
                    // Try to get the latest commit
                    RevCommit latestCommit = revWalk.parseCommit(git.getRepository().findRef("HEAD").getObjectId());

                    // If we can't get a commit, then it's not a Git repository
                    if (latestCommit == null) {
                        isGitRepo = false;
                    }
                } catch (Exception e) {
                    // An error also means that it's not a Git repository
                    isGitRepo = false;
                }
            }

            // If it's not a Git repository, initialize one
            if (!isGitRepo) {
                Git.init().setDirectory(directory.toFile()).call();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
