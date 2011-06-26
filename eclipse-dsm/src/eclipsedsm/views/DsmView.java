package eclipsedsm.views;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.dtangler.javaengine.types.JavaScope;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import eclipsedsm.model.TableModel;

public class DsmView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "eclipsedsm.views.DsmView";

	// TODO Maybe this value should be taken from somewhere?
	private static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	private TableViewer viewer;
	private TableModel model;

	//	private DependencyGraph items;
	//	private Dependable[] itemIndexes;

	/**
	 * The constructor.
	 */
	public DsmView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		setItems();
		setColumns();
		viewer.getTable().setHeaderVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(model);

		viewer.setInput(model.getRows());

		// attachCellEditors(viewer, viewer.getTable());
	}

	private void setColumns() {
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, 75, true));
		for (int i = 0; i < model.getColumnNames().size(); i++) {
			layout.addColumnData(new ColumnWeightData(50, 75, true));
		}
		viewer.getTable().setLayout(layout);

		new TableColumn(viewer.getTable(), SWT.CENTER).setText("Class");
		for (String dep : model.getColumnNames()) {
			new TableColumn(viewer.getTable(), SWT.CENTER).setImage(GraphicsUtils.createRotatedText(dep, viewer
					.getTable().getFont(), viewer.getTable().getForeground(), SWT.UP));
		}
	}

	private void setItems() {
		//JavaCore javaCore = JavaCore.getJavaCore();
		String workspacePath = Platform.getInstanceLocation().getURL().getPath();
		try {
			List<String> projectPaths = new ArrayList<String>();
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if (!project.isOpen() || !project.isNatureEnabled(JAVA_NATURE)) {
					continue;
				}

				IJavaProject javaProject = JavaCore.create(project);

				String projectAbsolutePath = workspacePath.subSequence(0, workspacePath.length() - 1)
						+ javaProject.getOutputLocation().toOSString();

				projectPaths.add(projectAbsolutePath);

			}
			analyze(projectPaths);
		} catch (CoreException e) {
			// TODO
			e.printStackTrace();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void analyze(List<String> paths) {
		Arguments arguments = new Arguments();
		arguments.setDependencyEngineId("java");
		arguments.setInput(paths);

		Dependencies deps = new JavaDependencyEngine().getDependencies(arguments);
		model = new TableModel(deps.getDependencyGraph(JavaScope.classes));
		//		items = deps.getDependencyGraph(JavaScope.classes);
		//		itemIndexes = items.getAllItems().toArray(new Dependable[items.getAllItems().size()]);

		//AnalysisResult analyze = new ConfigurableDependencyAnalyzer(arguments).analyze(items);
	}
}