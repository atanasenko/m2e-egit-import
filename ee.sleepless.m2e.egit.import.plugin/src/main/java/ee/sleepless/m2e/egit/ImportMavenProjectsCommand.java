/*******************************************************************************
 * Copyright (c) 2010, 2013 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *******************************************************************************/
package ee.sleepless.m2e.egit;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.repository.tree.FolderNode;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.wizards.MavenImportWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Implements "Add Projects" for Repository, Working Directory, and Folder
 */
@SuppressWarnings({ "rawtypes", "restriction" })
public class ImportMavenProjectsCommand extends AbstractHandler {
    public Object execute(ExecutionEvent event) throws ExecutionException {
        List<RepositoryTreeNode> selectedNodes = getSelectedNodes(event);
        if (selectedNodes == null || selectedNodes.isEmpty()) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    UIText.ImportProjectsWrongSelection,
                    UIText.ImportProjectsSelectionInRepositoryRequired);
            return null;
        }
        
        if (!(((List) selectedNodes).get(0) instanceof RepositoryTreeNode)) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    UIText.ImportProjectsWrongSelection,
                    UIText.ImportProjectsSelectionInRepositoryRequired);
            return null;
        }
        
        RepositoryTreeNode node = selectedNodes.get(0);
        String path;

        switch (node.getType()) {
        case REPO:
            // fall through
        case WORKINGDIR:
            path = node.getRepository().getWorkTree().toString();
            break;
        case FOLDER:
            path = ((FolderNode) node).getObject().getPath().toString();
            break;
        default:
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    UIText.ImportProjectsWrongSelection,
                    UIText.ImportProjectsSelectionInRepositoryRequired);
            return null;
        }

        WizardDialog dlg = new WizardDialog(HandlerUtil.getActiveShell(event),
                new MavenImportWizard(new ProjectImportConfiguration(), Collections.singletonList(path)));
        dlg.open();

        return null;
    }

    @SuppressWarnings("unchecked")
    public List<RepositoryTreeNode> getSelectedNodes(ExecutionEvent event)
            throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection)
            return ((IStructuredSelection) selection).toList();
        else
            return Collections.emptyList();
    }

}
