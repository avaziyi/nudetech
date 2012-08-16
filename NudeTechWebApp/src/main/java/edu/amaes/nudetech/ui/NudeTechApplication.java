package edu.amaes.nudetech.ui;

import edu.amaes.nudetech.ui.utililities.MessagesUtil;
import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

/**
 *
 * @author Angelo Balaguer
 */
public class NudeTechApplication extends Application {
    
    private static final String THEME_NUDETECH = "nudetech";
    private static final String APPLICATION_TITLE = "application.title";
    private static final String PANEL_TITLE_ABOUT = "panel.title.about";
    private static final String PANEL_TITLE_HELP = "panel.title.help";
    
    private Window mainWindow;
    private Panel mainPanel;

    @Override
    public void init() {
        setTheme(THEME_NUDETECH);
        
        mainWindow = new Window(MessagesUtil.getMessage(APPLICATION_TITLE));
        setMainWindow(mainWindow);

        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        mainWindow.setContent(rootLayout);
        
        mainPanel = new Panel(MessagesUtil.getMessage(APPLICATION_TITLE));
        mainPanel.setWidth("80%");
        mainPanel.setHeight("80%");

        HorizontalLayout mainPanelLayout = new HorizontalLayout();
        mainPanelLayout.setSizeFull();
        mainPanel.setContent(mainPanelLayout);

        FileUploaderPanel controlPanel = new FileUploaderPanel();
        
        Embedded aboutPanel = new Embedded("aboutTxt", new ThemeResource("info/about.html"));
        aboutPanel.setType(Embedded.TYPE_BROWSER);
        aboutPanel.setWidth("100%");
        aboutPanel.setHeight("100%");
        
        Embedded helpPanel = new Embedded("helpTxt", new ThemeResource("info/help.html"));
        helpPanel.setType(Embedded.TYPE_BROWSER);
        helpPanel.setWidth("100%");
        helpPanel.setHeight("100%");

        TabSheet infoTabSheet = new TabSheet();
        infoTabSheet.setWidth("100%");
        infoTabSheet.setHeight("100%");

        Resource aboutIcon = new ThemeResource("img/about_icon.gif");
        Resource helpIcon = new ThemeResource("img/help_icon.gif");

        infoTabSheet.addTab(aboutPanel, MessagesUtil.getMessage(PANEL_TITLE_ABOUT), aboutIcon);
        infoTabSheet.addTab(helpPanel, MessagesUtil.getMessage(PANEL_TITLE_HELP), helpIcon);

        mainPanelLayout.addComponent(controlPanel);
        mainPanelLayout.addComponent(infoTabSheet);

        rootLayout.addComponent(mainPanel);
        rootLayout.setComponentAlignment(mainPanel, Alignment.MIDDLE_CENTER);
    }
}
