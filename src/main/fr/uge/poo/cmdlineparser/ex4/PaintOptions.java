package fr.uge.poo.cmdlineparser.ex4;

import java.net.InetSocketAddress;
import java.util.Objects;

class PaintOptions {
    private final String windowName;
    private final boolean legacy;
    private final boolean bordered;
    private final int windowWidth;
    private final int windowHeight;
    private final int borderWidth;
    private final InetSocketAddress serv;

    private PaintOptions(PaintOptionsBuilder optionsBuilder) {
        this.bordered = optionsBuilder.bordered;
        this.borderWidth = optionsBuilder.borderWidth;
        this.legacy = optionsBuilder.legacy;
        this.serv = optionsBuilder.serv;
        this.windowHeight = optionsBuilder.windowHeight;
        this.windowName = optionsBuilder.windowName;
        this.windowWidth = optionsBuilder.windowWidth;
    }

    @Override
    public String toString() {
        return "PaintOptions [ bordered = " + bordered + " bordered-width = " + borderWidth + ", legacy = " + legacy + ", serv = " + serv + ", window-name = " + windowName + ", window-width = " + windowWidth + ", window-height = " + windowHeight + " ]";
    }

    static class PaintOptionsBuilder {

        private String windowName;
        private boolean legacy = false;
        private boolean bordered = false;
        private int windowWidth = 500;
        private int windowHeight = 500;
        private int borderWidth = 10;
        private InetSocketAddress serv;

        public PaintOptionsBuilder setWindowName(String windowName) {
            this.windowName = windowName;
            return this;
        }

        public PaintOptionsBuilder setLegacy(boolean legacy) {
            this.legacy = legacy;
            return this;
        }

        public PaintOptionsBuilder setBordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public PaintOptionsBuilder setWindowWidth(int windowWidth) {
            this.windowWidth = windowWidth;
            return this;
        }

        public PaintOptionsBuilder setWindowHeight(int windowHeight) {
            this.windowHeight = windowHeight;
            return this;
        }

        public PaintOptionsBuilder setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public PaintOptionsBuilder setServ(InetSocketAddress serv) {
            Objects.requireNonNull(serv);
            this.serv = serv;
            return this;
        }

        public PaintOptions build() {
            if (windowName == null)
                throw new IllegalStateException();
            return new PaintOptions(this);
        }
    }
}
