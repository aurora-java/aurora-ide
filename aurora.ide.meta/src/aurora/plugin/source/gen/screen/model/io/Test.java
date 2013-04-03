package aurora.plugin.source.gen.screen.model.io;

import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.NumberField;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TextField;

public class Test {
	ScreenBody demoData(){
		ScreenBody sb = new ScreenBody();
		Form form= new Form();
		sb.addChild(form);
		form.getDataset().setModel("a.b.c.d");
		HBox hb = new HBox();
		form.addChild(hb);
		TextField tf = new TextField();
		hb.addChild(tf);
		NumberField nf = new NumberField();
		hb.addChild(nf);
		Button b = new Button();
		
		//form (ds Hbox( text(dsf) ,number,button(c1)) vbox( comobo,lov,button(c2)) fieldset( checkbox,label,button(c3)),button(c4),datetimer )
		//,button(c5),date
		
		//grid(toolbar,navbar,selectioncol,ds,column(dsf,renderer1),column(renderer5),column(renderer4),column(renderer3),column(renderer2),column(footrenderer),column(editor))
		
		//tab1(form),tab2(grid)
		
		
		return sb;
	}
	
	
}
