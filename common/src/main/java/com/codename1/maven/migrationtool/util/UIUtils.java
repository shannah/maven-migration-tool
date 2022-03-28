package com.codename1.maven.migrationtool.util;

import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;

import static com.codename1.ui.ComponentSelector.$;

public class UIUtils {
    public static void error(Component target, String message) {
        Log.p(message);
        Form form = target.getComponentForm();
        if (form == null) {
            ToastBar.showErrorMessage(message);
            return;
        }
        Container parent = findBoxlayoutYParent(target);
        Component row = findBoxlayoutYChild(target);
        if (parent == null || row == null) {
            ToastBar.showErrorMessage(message);
            return;
        }
        $("ErrorMessage", form).remove();
        $("ErrorContainer,ErrorTextField", form).each(c->{
            clearComponentError(c);
        });
        highlightComponentAsError(row);

        Label errorMessage = new Label(message, "ErrorMessage");
        parent.addComponent(parent.getComponentIndex(row)+1, errorMessage);

        form.animateHierarchy(500);
    }

    public static void highlightComponentAsError(Component cmp) {
        String uiid = cmp.getUIID();
        if (uiid != null && uiid.startsWith("Error") ) {
            return;
        }
        cmp.putClientProperty("X-UIID", uiid);
        if (cmp instanceof Container) {
            cmp.setUIID("ErrorContainer");
        } else {
            cmp.setUIID("ErrorTextField");
        }

    }

    public static void clearComponentError(Component cmp) {
        String uiid = cmp.getUIID();
        if ("ErrorContainer".equals(uiid) || "ErrorTextField".equals(uiid)) {
            String actual = (String)cmp.getClientProperty("X-UIID");
            if (actual != null) {
                cmp.setUIID(actual);
            }
        }
    }

    public static Container findBoxlayoutYParent(Component cmp) {
        Container parent = cmp.getParent();
        if (parent == null) return null;
        if (parent.getLayout() instanceof BoxLayout) {
            BoxLayout l = (BoxLayout)parent.getLayout();
            if (l.getAxis() == BoxLayout.Y_AXIS) {
                return parent;
            }
        }
        return findBoxlayoutYParent(parent);
    }

    public static Component findBoxlayoutYChild(Component cmp) {
        Container parent = cmp.getParent();
        if (parent == null) return null;
        if (parent.getLayout() instanceof BoxLayout) {
            BoxLayout l = (BoxLayout)parent.getLayout();
            if (l.getAxis() == BoxLayout.Y_AXIS) {
                return cmp;
            }
        }
        return findBoxlayoutYChild(parent);
    }

}
