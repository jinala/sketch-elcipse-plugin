package sketchPlugin.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Position;

public class Segment {
	private String name;
	public Position position;
	private Segment parent;
	private List children = new ArrayList();
	public Segment(String name,Position position){
		super();
		this.name = name;
		this.position = position;
	}
	public List getChildren(){
		return children;
	}
	public Segment addChildren(Segment child){
		children.add(child);
		child.setParent(this);
		return this;
	}
	public void setParent(Segment parent){
		this.parent = parent;
	}
	public Segment getParent(){
		return parent;
	}
	public String getName(){
		return name;
	}
	public void clear(){
		children.clear();
	}
	
	public String toString(){
		return name;
	}

}
