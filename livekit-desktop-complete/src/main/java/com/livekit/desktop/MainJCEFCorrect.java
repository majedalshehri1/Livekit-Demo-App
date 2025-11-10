package com.livekit.desktop;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * LiveKit Desktop with JCEF (Correct Initialization)
 *
 * Using jcefmaven for automatic native library handling
 */
public class MainJCEFCorrect {

    private static final String WEB_APP_URL = "http://localhost:5080";

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("LiveKit Desktop Application (JCEF - Correct Init)");
        System.out.println("=".repeat(70));
        System.out.println("\n⏳ Initializing JCEF (this may take a moment)...\n");

        try {
            // Use CefAppBuilder from jcefmaven - handles native libraries automatically!
            CefAppBuilder builder = new CefAppBuilder();

            // Configure settings
            builder.getCefSettings().windowless_rendering_enabled = false;

            // Add command line args for camera/mic support
            builder.addJcefArgs(
                    "--enable-media-stream",
                    "--use-fake-ui-for-media-stream",
                    "--enable-usermedia-screen-capturing",
                    "--disable-web-security",  // For localhost
                    "--allow-file-access-from-files"
            );

            // Set app handler
            builder.setAppHandler(new MavenCefAppHandlerAdapter() {
                @Override
                public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                    if (state == CefApp.CefAppState.TERMINATED) {
                        System.out.println("JCEF terminated");
                    }
                }
            });

            // Build CefApp
            CefApp cefApp = builder.build();

            System.out.println("JCEF initialized successfully!\n");

            // Create client
            CefClient client = cefApp.createClient();

            // Add console handler
            client.addDisplayHandler(new CefDisplayHandlerAdapter() {
                @Override
                public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level,
                                                String message, String source, int line) {
                    System.out.println("[Browser] " + message);
                    return false;
                }
            });

            // Add load handler
            client.addLoadHandler(new CefLoadHandlerAdapter() {
                @Override
                public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                    System.out.println("Page loaded: " + frame.getURL());
                    System.out.println("   Status: " + httpStatusCode);

                    // Test camera/mic access
                    String js =
                            "console.log('Testing camera and microphone access...');" +
                                    "navigator.mediaDevices.getUserMedia({video: true, audio: true})" +
                                    ".then(stream => {" +
                                    "  console.log('✅ Camera and microphone granted!');" +
                                    "  console.log('Video tracks:', stream.getVideoTracks().length);" +
                                    "  console.log('Audio tracks:', stream.getAudioTracks().length);" +
                                    "})" +
                                    ".catch(err => {" +
                                    "  console.error(' Media error:', err.name, err.message);" +
                                    "});";

                    frame.executeJavaScript(js, frame.getURL(), 0);
                }

                @Override
                public void onLoadError(CefBrowser browser, CefFrame frame,
                                        CefLoadHandler.ErrorCode errorCode,
                                        String errorText, String failedUrl) {
                    System.err.println(" Load error: " + errorText);
                    System.err.println("   URL: " + failedUrl);
                }
            });

            // Create browser
            CefBrowser browser = client.createBrowser(
                    WEB_APP_URL,
                    false,  // Not offscreen
                    false   // Not transparent
            );

            // Create window
            JFrame frame = new JFrame("LiveKit Desktop - Video Conference");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            // Add browser UI
            Component browserUI = browser.getUIComponent();
            frame.getContentPane().add(browserUI, BorderLayout.CENTER);

            // Handle window close
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("\n🛑 Closing application...");
                    browser.close(true);
                    CefApp.getInstance().dispose();
                    frame.dispose();
                    System.exit(0);
                }
            });

            // Show window
            frame.setVisible(true);

            System.out.println("\n✅ Application started successfully!");
            System.out.println("📷 Camera and microphone should work now");
            System.out.println("💡 Press F12 to open Developer Tools");
            System.out.println();

        } catch (Exception e) {
            System.err.println("\n❌ Failed to initialize JCEF:");
            e.printStackTrace();
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Check internet connection (JCEF downloads binaries)");
            System.err.println("2. Try: mvn clean install -U");
            System.err.println("3. Check antivirus isn't blocking downloads");
            System.exit(1);
        }
    }
}