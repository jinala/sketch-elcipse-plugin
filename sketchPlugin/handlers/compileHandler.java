package sketchPlugin.handlers;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;



import sketch.compiler.main.seq.SequentialSketchMain;
import sketch.util.exceptions.SketchException;

public class compileHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public compileHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = 
				page == null ? null : page.getActiveEditor();
		IEditorInput input = 
				editor == null ? null : editor.getEditorInput();
		IPath path = input instanceof FileEditorInput 
				? ((FileEditorInput)input).getPath()
						: null;
				IPath pathToFolder = path.removeLastSegments(1);
			//	System.out.println(pathToFolder.toString());

				MessageConsole myConsole = findConsole("Sketch Console");
				myConsole.clearConsole();

				MessageConsoleStream out = myConsole.newMessageStream();


				//System.setOut(new PrintStream(out));
				//System.setErr(new PrintStream(out));
				System.out.println("Hello");


				System.out.println("SKETCH version features: tprint, cuda-model, vlarrays");
				// long beg = System.currentTimeMillis();

				// TODO -- change class names so this is clear
				SequentialSketchMain sketchmain = new SequentialSketchMain(new String[]{"-P","preproc",path.toString()});
				try {
					sketchmain.run();
				} catch (SketchException e) {
					e.printStackTrace();

				} catch (java.lang.Error e) {
					e.printStackTrace();

				} catch (RuntimeException e) {
					e.printStackTrace();

				}
				//  out.println("Total time = " + (System.currentTimeMillis() - beg));


				IConsole console = myConsole;


				String id = IConsoleConstants.ID_CONSOLE_VIEW;
				IConsoleView view;
				try {
					view = (IConsoleView) page.showView(id);
					view.display(console);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
	}
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
}
