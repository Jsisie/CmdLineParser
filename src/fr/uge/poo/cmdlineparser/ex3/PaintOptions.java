package fr.uge.poo.cmdlineparser.ex3;

import java.net.InetSocketAddress;

public class PaintOptions {
    private final String windowName;
    private final boolean legacy;
    private final boolean bordered;
    private final int windowWidth;
    private final int windowHeight;
    private final int borderWidth;
    private final InetSocketAddress ss;

    private PaintOptions(PaintOptionsBuilder optionsBuilder) {
        this.bordered = optionsBuilder.bordered;
        this.borderWidth = optionsBuilder.borderWidth;
        this.legacy = optionsBuilder.legacy;
        this.ss = optionsBuilder.ss;
        this.windowHeight = optionsBuilder.windowHeight;
        this.windowName = optionsBuilder.windowName;
        this.windowWidth = optionsBuilder.windowWidth;
    }

    @Override
    public String toString() {
        return "PaintOptions [ bordered = " + bordered + " bordered-width = " + borderWidth + ", legacy = " + legacy + ", serv = " + ss + ", window-name = " + windowName + ", window-width = " + windowWidth + ", window-height = " + windowHeight + " ]";
    }

    public static class PaintOptionsBuilder {
        private String windowName;
        private boolean legacy = false;
        private boolean bordered = false;
        private int windowWidth = 500;
        private int windowHeight = 500;
        private int borderWidth = 10;
        private InetSocketAddress ss;

        public void setWindowName(String windowName) {
            this.windowName = windowName;
        }

        public void setLegacy(boolean legacy) {
            this.legacy = legacy;
        }

        public void setBordered(boolean bordered) {
            this.bordered = bordered;
        }

        public void setWindowWidth(int windowWidth) {
            this.windowWidth = windowWidth;
        }

        public void setWindowHeight(int windowHeight) {
            this.windowHeight = windowHeight;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        public void setSocketAddress(InetSocketAddress ss) {
            this.ss = ss;
        }

        public PaintOptions build() {
            if (windowName == null)
                throw new IllegalStateException();
            return new PaintOptions(this);
        }
    }
}
