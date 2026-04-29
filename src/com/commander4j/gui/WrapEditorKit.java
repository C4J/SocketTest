package com.commander4j.gui;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

class WrapEditorKit extends StyledEditorKit {
    private static final long serialVersionUID = 1L;
	ViewFactory defaultFactory=new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

}