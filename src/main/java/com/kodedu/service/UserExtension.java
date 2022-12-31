package com.kodedu.service;

import com.kodedu.helper.IOHelper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.DocinfoProcessor;
import org.asciidoctor.extension.ExtensionGroup;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.asciidoctor.extension.Postprocessor;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.Treeprocessor;
import org.asciidoctor.jruby.internal.JRubyRuntimeContext;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class UserExtension {

    private Logger logger = LoggerFactory.getLogger(UserExtension.class);

    private List<Path> extensions = new ArrayList<>();
    private ExtensionGroup extensionGroup;

    private List<Class> extensionClasses = List.of(BlockMacroProcessor.class, BlockProcessor.class, DocinfoProcessor.class, IncludeProcessor.class,
            InlineMacroProcessor.class, Postprocessor.class, Preprocessor.class, Treeprocessor.class);
    private List<String> extensionSuperclasses = extensionClasses.stream().map(e -> "Asciidoctor::Extensions::" + e.getSimpleName()).toList();

    private ExecutorService executorService;

    public void setExtensionGroup(ExtensionGroup extensionGroup) {
        this.extensionGroup = extensionGroup;
    }

    public ExtensionGroup getExtensionGroup() {
        return extensionGroup;
    }

    public List<Path> getExtensions() {
        return extensions;
    }

    static {
        ClassGraph.CIRCUMVENT_ENCAPSULATION = ClassGraph.CircumventEncapsulationMethod.JVM_DRIVER;
    }

    public void registerExtensions(Asciidoctor adoc, List<Path> extensions) {
        if (extensions.equals(this.extensions)) {
            return;
        }
        extensionGroup.unregister();
        if (!extensions.isEmpty()) {
            try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();) {
                executorService.submit(() -> registerRubyExtensions(adoc, extensions));
                executorService.submit(() -> registerJavaExtensions(adoc, extensions));
            }
            extensionGroup.register();
        }
        this.extensions = extensions;
    }

    private void registerJavaExtensions(Asciidoctor adoc, List<Path> extensions) {
        URL[] urls = extensions.stream()
                .filter(p -> p.toString().endsWith(".jar"))
                .map(p -> IOHelper.toURL(p))
                .filter(Objects::nonNull)
                .toArray(size -> new URL[size]);
        try (ScanResult scanResult = new ClassGraph()
                .addClassLoader(new URLClassLoader(urls))
                .enableClassInfo()
                .scan(getExecutorService(), 8)) {
            extensionClasses.stream()
                    .map(c -> scanResult.getSubclasses(c))
                    .flatMap(c -> c.stream())
                    .filter(c -> !c.getPackageInfo().getName().startsWith("com.kodedu"))
                    .filter(c -> c.getSubclasses().isEmpty())
                    .forEach(classInfo -> {
                        try {
                            Class<?> extensionClass = classInfo.loadClass();
                            logger.info("Loading {} extension", classInfo.getSimpleName());
                            if (BlockMacroProcessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.blockMacro((Class<? extends BlockMacroProcessor>) extensionClass);
                            } else if (BlockProcessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.block((Class<? extends BlockProcessor>) extensionClass);
                            } else if (DocinfoProcessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.docinfoProcessor((Class<? extends DocinfoProcessor>) extensionClass);
                            } else if (IncludeProcessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.includeProcessor((Class<? extends IncludeProcessor>) extensionClass);
                            } else if (InlineMacroProcessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.inlineMacro((Class<? extends InlineMacroProcessor>) extensionClass);
                            } else if (Postprocessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.postprocessor((Class<? extends Postprocessor>) extensionClass);
                            } else if (Preprocessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.preprocessor((Class<? extends Preprocessor>) extensionClass);
                            } else if (Treeprocessor.class.isAssignableFrom(extensionClass)) {
                                extensionGroup.treeprocessor((Class<? extends Treeprocessor>) extensionClass);
                            } else {
                                logger.warn("Extension type not found: {}", classInfo.getSimpleName());
                            }
                        } catch (Exception e) {
                            logger.error("Loading {} extension has failed", classInfo.getSimpleName(), e);
                        }
                    });
        }
    }

    private ExecutorService getExecutorService() {
        if (Objects.isNull(executorService)) {
            this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        }
        return executorService;
    }

    private void registerRubyExtensions(Asciidoctor adoc, List<Path> extensions) {
        Ruby ruby = JRubyRuntimeContext.get(adoc);
        List<Path> rubyExtensions = extensions.stream().filter(p -> p.toString().endsWith(".rb")).collect(Collectors.toList());
        for (Path rubyExtension : rubyExtensions) {
            try (InputStream inputStream = new FileInputStream(rubyExtension.toFile())) {
                extensionGroup.loadRubyClass(inputStream);
//                RubyEnumerator rubyEnumerator = (RubyEnumerator) ruby.getModule("ObjectSpace")
//                        .callMethod("each_object", ruby.getClass("Class"));
                RubyModule objectModule = ruby.getModule("Object");
                extensionSuperclasses.stream()
                        .map(c -> objectModule.const_get(ruby.newString(c)))
                        .filter(c -> c instanceof RubyClass)
                        .map(c -> (RubyClass) c)
                        .forEach(c -> {
                            c.subclasses(true)
                                    .stream()
                                    .filter(cc -> !cc.getName().contains("Asciidoctor::"))
                                    .filter(cc -> cc.subclasses(true).isEmpty()) // only load leaf classes
                                    .forEach(cc -> {
                                        String className = cc.getName();
                                        String extensionType = c.getBaseName();
                                        logger.info("Loading {} extension", className);
                                        switch (extensionType) {
                                            case "BlockMacroProcessor" -> extensionGroup.rubyBlockMacro(className);
                                            case "BlockProcessor" -> extensionGroup.rubyBlock(className);
                                            case "DocinfoProcessor" -> extensionGroup.rubyDocinfoProcessor(className);
                                            case "IncludeProcessor" -> extensionGroup.rubyIncludeProcessor(className);
                                            case "InlineMacroProcessor" -> extensionGroup.rubyInlineMacro(className);
                                            case "Postprocessor" -> extensionGroup.rubyPostprocessor(className);
                                            case "Preprocessor" -> extensionGroup.rubyPreprocessor(className);
                                            case "Treeprocessor" -> extensionGroup.rubyTreeprocessor(className);
                                            default -> logger.warn("Extension type not found: {}", extensionType);
                                        }
                                    });
                        });

            } catch (Exception e) {
                logger.error("Loading {} extension has failed", rubyExtension, e);
            }
        }
    }
}
