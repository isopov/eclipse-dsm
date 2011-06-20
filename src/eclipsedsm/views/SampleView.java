package eclipsedsm.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "eclipsedsm.views.SampleView";

	// TODO Maybe this value should be taken from somewhere?
	private static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";
	
	private static final Object[] CONTENT = new Object[] {
		new EditableTableItem("item 1", new Integer(0)),
		new EditableTableItem("item 2", new Integer(1)) };

private static final String[] VALUE_SET = new String[] { "xxx", "yyy",
		"zzz" };

private static final String NAME_PROPERTY = "name";

private static final String VALUE_PROPERTY = "value";

private TableViewer viewer;


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
		try {
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
					.getProjects()) {
				if (!project.isOpen() || !project.isNatureEnabled(JAVA_NATURE)) {
					continue;
				}

				// Use this to obtain path to class file in filesystem
				// new File(IResource.getLocation().toOSString());

			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, 75, true));
		layout.addColumnData(new ColumnWeightData(50, 75, true));
		viewer.getTable().setLayout(layout);

		TableColumn nameColumn = new TableColumn(viewer.getTable(), SWT.CENTER);
		nameColumn.setText("Project");
		TableColumn valColumn = new TableColumn(viewer.getTable(), SWT.CENTER);
		valColumn.setText("Value");
		viewer.getTable().setHeaderVisible(true);

		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		attachCellEditors(viewer, viewer.getTable());

		MenuManager popupMenu = new MenuManager();
		IAction newRowAction = new NewRowAction();
		popupMenu.add(newRowAction);
		Menu menu = popupMenu.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(menu);

		viewer.setInput(CONTENT);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "eclipse-dsm.viewer");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Checks whether the given resource is a Java class file.
	 * 
	 * Copy/Paste from FindBugs Eclipse plugin
	 * 
	 * @param resource
	 *            The resource to check.
	 * @return <code>true</code> if the given resource is a class file,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isClassFile(IResource resource) {
		if (resource == null || (resource.getType() != IResource.FILE)) {
			return false;
		}
		String ex = resource.getFileExtension();
		return "class".equalsIgnoreCase(ex); //$NON-NLS-1$

	}

	private class NewRowAction extends Action {
		public NewRowAction() {
			super("Insert New Row");
		}

		public void run() {
			EditableTableItem newItem = new EditableTableItem("new row",
					new Integer(2));
			viewer.add(newItem);
		}
	}

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((EditableTableItem) element).name;
				case 1:
					Number index = ((EditableTableItem) element).value;
					return VALUE_SET[index.intValue()];
				default:
					return "Invalid column: " + columnIndex;
				}
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener lpl) {
			}
		});
	}

	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) {
		viewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				if (NAME_PROPERTY.equals(property))
					return ((EditableTableItem) element).name;
				else
					return ((EditableTableItem) element).value;
			}

			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				EditableTableItem data = (EditableTableItem) tableItem
						.getData();
				if (NAME_PROPERTY.equals(property))
					data.name = value.toString();
				else
					data.value = (Integer) value;

				viewer.refresh(data);
			}
		});

		viewer.setCellEditors(new CellEditor[] { new TextCellEditor(parent),
				new ComboBoxCellEditor(parent, VALUE_SET) });

		viewer.setColumnProperties(new String[] { NAME_PROPERTY, VALUE_PROPERTY });
	}

	private static class EditableTableItem {
		public String name;

		public Integer value;

		public EditableTableItem(String n, Integer v) {
			name = n;
			value = v;
		}
	}
}