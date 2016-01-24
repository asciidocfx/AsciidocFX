package com.kodcu.commandline;

import de.tototec.cmdoption.CmdlineModel;
import de.tototec.cmdoption.CommandHandle;
import de.tototec.cmdoption.DefaultUsageFormatter;
import de.tototec.cmdoption.OptionHandle;
import de.tototec.cmdoption.internal.I18n;
import de.tototec.cmdoption.internal.I18nFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Hakan on 11/30/2015.
 */
public class AfxUsageFormatter extends DefaultUsageFormatter {

    private final I18n i18n = I18nFactory.getI18n(AfxUsageFormatter.class);

    public AfxUsageFormatter(boolean withCommandDetails, int lineLength) {
        super(withCommandDetails, lineLength);
    }

    public void format(final StringBuilder output, final CmdlineModel cmdlineModel) {

        final ArrayList<OptionHandle> sortedOptions = cmdlineModel.getOptions().stream()
                .filter(op -> !op.isHidden())
                .collect(Collectors.toCollection(ArrayList<OptionHandle>::new));
        Collections.sort(sortedOptions, new OptionHandle.OptionHandleComparator());

        final ArrayList<CommandHandle> sortedCommands = cmdlineModel.getCommands().stream()
                .filter(op -> !op.isHidden())
                .collect(Collectors.toCollection(ArrayList<CommandHandle>::new));

        Collections.sort(sortedCommands, new CommandHandle.CommandHandleComparator());
        // Usage
        output.append(i18n.tr("Usage:")).append("  ");
        output.append(cmdlineModel.getProgramName() == null ? i18n.tr("program") : cmdlineModel.getProgramName());
        output.append(" ").append(i18n.tr("[parameter...]"));
        output.append("\n\t\t");
        output.append(cmdlineModel.getProgramName() == null ? i18n.tr("program") : cmdlineModel.getProgramName());
        output.append(" ").append(i18n.tr("[options..."));
        output.append(" ").append(i18n.tr("file]"));
        output.append("\n\t\t");
        output.append(cmdlineModel.getProgramName() == null ? i18n.tr("program") : cmdlineModel.getProgramName());
        output.append(" ").append(i18n.tr("[ --help | -h | --version ]"));

        // about cmd
        printAbout(cmdlineModel, output);
        formatOptions(output, sortedOptions, "\n" + i18n.tr("Options:"), cmdlineModel.getResourceBundle());
        // Parameters
        formatParameter(output, cmdlineModel.getParameter(), "\n" + i18n.tr("Parameter:"), cmdlineModel.getResourceBundle());
    }

    protected void printAbout(final CmdlineModel cmdlineModel, final StringBuilder output) {
        // About
        if (cmdlineModel.getAboutLine() != null && cmdlineModel.getAboutLine().length() > 0) {
            output.append(translate(cmdlineModel.getResourceBundle(), cmdlineModel.getAboutLine())).append("\n");
        }
    }

}
