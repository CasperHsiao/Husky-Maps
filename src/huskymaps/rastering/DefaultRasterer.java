package huskymaps.rastering;

import huskymaps.graph.Coordinate;
import huskymaps.utils.Constants;

/**
 * @see Rasterer
 */
public class DefaultRasterer implements Rasterer {
    public TileGrid rasterizeMap(Coordinate ul, Coordinate lr, int depth) {
        double W = lr.lon() - ul.lon();
        double L = ul.lat() - lr.lat();
        double w = Constants.LON_PER_TILE[depth];
        double l = Constants.LAT_PER_TILE[depth];

        double lonToRoot = (ul.lon() - Constants.ROOT_ULLON);
        double latToRoot = (Constants.ROOT_ULLAT - ul.lat());

        boolean contains = contains(true, lonToRoot, latToRoot, ul, lr);


        int numX = (int) (lonToRoot/w);
        int numY = (int) (latToRoot/l);

        double overshootX = (numX + 1 - lonToRoot/w)*w;
        double overshootY = (numY + 1 - latToRoot/l)*l;

        if (lonToRoot < 0) {
            numX = 0;
            overshootX = w - lonToRoot;
        }
        if (latToRoot < 0) {
            numY = 0;
            overshootY = l - latToRoot;
        }

        double width = ((W - overshootX)/w);
        double length = ((L - overshootY)/l);
        int lengthX = (int) width + 1;
        int lengthY = (int) length + 1;
        if (width <= 0) {
            lengthX = 0;
        } else if (width % 1 == 0) {
            lengthX--;
        }
        if (length <= 0) {
            lengthY = 0;
        } else if (length % 1 == 0) {
            lengthY--;
        }

        if (lr.lon() > Constants.ROOT_LRLON) {
            lengthX = Constants.NUM_X_TILES_AT_DEPTH[depth] - numX - 1;
        }
        if (lr.lat() < Constants.ROOT_LRLAT) {
            lengthY = Constants.NUM_Y_TILES_AT_DEPTH[depth] - numY - 1;
        }


        if (contains) {
            return helperFunction(lengthY, lengthX, depth, numX, numY);
        }
        return new TileGrid(null);
    }

    private TileGrid helperFunction(int lengthY, int lengthX, int depth, int numX, int numY) {
        Tile[][] grid = new Tile[lengthY + 1][lengthX + 1];
        for (int y = 0; y < lengthY + 1; y++) {
            grid[y][0] = new Tile(depth, numX, numY + y);
            for (int x = 1; x < lengthX + 1; x++) {
                grid[y][x] = new Tile(depth, numX + x, numY + y);
            }
        }
        return new TileGrid(grid);
    }

    private boolean contains(boolean contains, double lonToRoot, double latToRoot, Coordinate ul,
                             Coordinate lr) {
        if (lonToRoot < 0) {
            if (lr.lon() <= Constants.ROOT_ULLON) {
                contains = false;
            }
        }
        if (contains) {
            if (lr.lon() > Constants.ROOT_LRLON) {
                if (ul.lon() >= Constants.ROOT_LRLON) {
                    contains = false;
                }
            }
        }
        if (contains) {
            if (latToRoot < 0) {
                if (lr.lat() >= Constants.ROOT_ULLAT) {
                    contains = false;
                }
            }
        }
        if (contains) {
            if (lr.lat() < Constants.ROOT_LRLAT) {
                if (ul.lat() <= Constants.ROOT_LRLAT) {
                    contains = false;
                }
            }
        }
        return contains;
    }
}
