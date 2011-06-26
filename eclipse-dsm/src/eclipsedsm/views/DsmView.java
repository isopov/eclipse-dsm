package eclipsedsm.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import eclipsedsm.model.ColumnElement;
import eclipsedsm.model.RowElement;
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
	private Map<Image, String> images = new HashMap<Image, String>();

	private List<TableEditor> editors = new ArrayList<TableEditor>();

	private SelectionAdapter rowListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			RowElement vertical = model.getRowElementByName(((Button) event.getSource()).getText());
			if (vertical.isCollapsible()) {
				vertical.setCollapsed(!vertical.isCollapsed());
				for (TableEditor editor : editors) {
					editor.getEditor().dispose();
					editor.dispose();
				}
				editors.clear();
				viewer.getTable().removeAll();

				viewer.setInput(model.getRows());
				attachedRowListeners(viewer.getTable());
				viewer.refresh();
			}
		}
	};

	private Listener columnListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			ColumnElement element = model.getColumnElementByName(images.get(((TableColumn) event.widget).getImage()));
			if (element.isCollapsible()) {
				element.setCollapsed(!element.isCollapsed());
				updateCollapsedColumns();
			}
		}
	};

	/**
	 * The constructor.
	 */
	public DsmView() {
	}

	private void updateCollapsedColumns() {
		for (int i = 1; i < viewer.getTable().getColumns().length; i++) {
			TableColumn column = viewer.getTable().getColumns()[i];
			String name = images.get(column.getImage());
			ColumnElement columnElement = model.getColumnElementByName(name);
			if (columnElement.isColumnVisible()) {
				column.setWidth(40);
			} else {
				column.setWidth(0);
			}
		}
		viewer.refresh();

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		setModel();
		setColumns();
		Table table = viewer.getTable();
		table.setHeaderVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(model);

		viewer.setInput(model.getRows());

		attachedRowListeners(table);

	}

	private void attachedRowListeners(Table table) {
		for (TableItem item : table.getItems()) {
			TableEditor tableEditor = new TableEditor(table);
			Button button = new Button(table, SWT.PUSH);
			button.setAlignment(SWT.LEFT);
			button.setText(item.getText());
			button.computeSize(SWT.DEFAULT, table.getItemHeight());

			tableEditor.grabHorizontal = true;
			tableEditor.minimumHeight = button.getSize().y;
			tableEditor.minimumWidth = button.getSize().x;

			tableEditor.setEditor(button, item, 0);
			editors.add(tableEditor);
			button.addSelectionListener(rowListener);
		}
	}

	private void setColumns() {
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(0, 75, true));
		for (int i = 0; i < model.getColumnNames().size(); i++) {
			layout.addColumnData(new ColumnPixelData(40, false));
		}
		viewer.getTable().setLayout(layout);
		images.clear();

		new TableColumn(viewer.getTable(), SWT.LEFT).setText("Class");
		for (String dep : model.getColumnNames()) {
			TableColumn column = new TableColumn(viewer.getTable(), SWT.CENTER);
			column.setResizable(false);
			Image image = GraphicsUtils.createRotatedText(dep, viewer.getTable().getFont(), viewer.getTable()
					.getForeground(), SWT.UP);
			column.setImage(image);
			images.put(image, dep);
			column.addListener(SWT.Selection, columnListener);
		}
		updateCollapsedColumns();
	}

	private void setModel() {
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

			Arguments arguments = new Arguments();
			arguments.setDependencyEngineId("java");
			arguments.setInput(projectPaths);

			Dependencies deps = new JavaDependencyEngine().getDependencies(arguments);
			model = new TableModel(deps.getDependencyGraph(JavaScope.classes));
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
}