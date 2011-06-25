package eclipsedsm.views;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.dtangler.javaengine.types.JavaScope;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "eclipsedsm.views.SampleView";

	// TODO Maybe this value should be taken from somewhere?
	private static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	private TableViewer viewer;
	private DependencyGraph items;
	private Dependable[] itemIndexes;

	/**
	 * The constructor.
	 */
	public SampleView() {
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

		attachContentProvider(viewer);
		attachLabelProvider(viewer);

		viewer.setInput(itemIndexes);

		// attachCellEditors(viewer, viewer.getTable());
	}

	private void setColumns() {
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, 75, true));
		for (int i = 0; i < itemIndexes.length; i++) {
			layout.addColumnData(new ColumnWeightData(50, 75, true));
		}
		viewer.getTable().setLayout(layout);

		new TableColumn(viewer.getTable(), SWT.CENTER).setText("Class");
		for (Dependable dep : itemIndexes) {
			//			new TableColumn(viewer.getTable(), SWT.CENTER).setText(dep.getDisplayName());
			new TableColumn(viewer.getTable(), SWT.CENTER).setImage(GraphicsUtils.createRotatedText(
					dep.getDisplayName(), viewer.getTable().getFont(), viewer.getTable().getForeground(), SWT.UP));
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

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new TableLabelProvider());
	}

	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
	}

	private void analyze(List<String> paths) {
		Arguments arguments = new Arguments();
		arguments.setDependencyEngineId("java");
		arguments.setInput(paths);

		Dependencies deps = new JavaDependencyEngine().getDependencies(arguments);
		items = deps.getDependencyGraph(JavaScope.classes);
		itemIndexes = items.getAllItems().toArray(new Dependable[items.getAllItems().size()]);

		//AnalysisResult analyze = new ConfigurableDependencyAnalyzer(arguments).analyze(items);
	}

	private final class TableLabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Dependable item = (Dependable) element;
			if (columnIndex == 0) {
				return item.getDisplayName();
			} else {
				int otherIndex = 0;
				for (Dependable otherDep : itemIndexes) {
					if (columnIndex == ++otherIndex) {
						if (item == otherDep) {
							return "-";
						}
						return String.valueOf(items.getDependencyWeight(item, otherDep));
					}
				}
			}

			return "Invalid column: " + columnIndex;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener lpl) {
		}
	}
}