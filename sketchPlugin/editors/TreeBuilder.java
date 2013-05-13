package sketchPlugin.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtEmpty;
import sketch.compiler.ast.core.stmts.StmtFunDecl;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.typs.Type;

public class TreeBuilder extends FEReplacer{
	IDocument document;
	int offset;
	int length;
	int line=0;
	
	public TreeBuilder(IDocument document, int offset , int length){
		this.document = document;
		this.offset = offset;
		this.length = length;
		
		try {
			line = document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public Segment visitFunction(Function func,Segment parent){
		
		Segment seg = new Segment("Function: "+func.getName()+"( "+func.printParams()+" ): "+func.getReturnType(),new Position(offset,length));
		if(parent!= null){
			parent.addChildren(seg);
		}
		parent = seg;
		//System.out.println(func.getBody());
		visitStmtBlock((StmtBlock)func.getBody(),seg);        
        return seg;
	}
	
	 public void visitStmtBlock(StmtBlock stmt,Segment parent)
	 {    
	       	        
	       List<Statement> stmts = stmt.getStmts();
	        for (Statement s : stmts)
	        {	            
	            
	           if(s instanceof StmtFunDecl)
	        	   
	            	visitStmtFunDecl((StmtFunDecl) s,parent);         	
	                
	        }	        
	       
	    }
	
	public void visitStmtFunDecl(StmtFunDecl stmt, Segment parent) {
		int line1 = line+ stmt.getDecl().getCx().getLineNumber() -1;
		try {
			offset = document.getLineOffset(line1);
			length = document.getLineLength(line1);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        visitFunction(stmt.getDecl(),parent);
        
    }

}
