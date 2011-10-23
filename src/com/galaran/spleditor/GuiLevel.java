package com.galaran.spleditor;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GuiLevel {
    public final File dir;
    
    private long LastPlayed;
    
    public String name;
    public long seed;
    
    public Dimension dimension;
    public int posX;
    public int posY;
    public int posZ;
    
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    
    public long time;
    public boolean raining;
    public boolean thundering;
    public int rainTime;
    public int thunderTime;
    
    public short hp;
    public int food;
    public int level;
    
    public GameMode gameMode;

    public GuiLevel(File dir) throws Exception {
        this.dir = dir;
        
        load();
    }
    
    private final void load() throws Exception {        
        NbtLevel tl = loadNbtLevel();

        // update values
        LastPlayed = tl.getLastPlayed();
        
        name = tl.getName();
        seed = tl.getSeed();
        
        dimension = Dimension.getByNbtVal(tl.getDimension());
        if (dimension == null) throw new Exception("Invalid dimension");
        posX = (int) tl.getPosX();
        posY = (int) tl.getPosY();
        posZ = (int) tl.getPosZ();
        
        spawnX = tl.getSpawnX();
        spawnY = tl.getSpawnY();
        spawnZ = tl.getSpawnZ();
        
        time = tl.getTime();
        raining = tl.getRaining() == 1 ? true : false;
        thundering = tl.getThundering() == 1 ? true : false;
        rainTime = tl.getRainTime();
        thunderTime = tl.getThunderTime();
        
        hp = (Short) tl.getHp();
        food = tl.getFood();
        level = tl.getLevel();
        
        gameMode = GameMode.getByNbtVals(tl.getGameType(), tl.getHardcore());
        if (gameMode == null) throw new Exception("Invalid Game Mode");
    }
    
    public void save() throws IOException {
        NbtLevel nbtLev = loadNbtLevel();
        
        nbtLev.mergeWith(this);
        
        try {
            nbtLev.save(dir);
        } catch (IOException ex) {
            throw new IOException("Error saving level");
        }
    }
    
    @Override
    public String toString() {
        return dir.getName();
    }

    private NbtLevel loadNbtLevel() throws IOException {
        NbtLevel nbtLev = null;
        try {
            nbtLev = new NbtLevel(dir);
        } catch (Exception ex) {
            throw new IOException("Level has been deleted");
        }
        
        return nbtLev;
    }
    
    public static enum Dimension {
        OVERWORLD(0),
        NETHER(-1),
        THE_END(1);
        
        private final int nbtVal;
        
        private Dimension(int nbtVal) {
            this.nbtVal = nbtVal;
        }
        
        Integer getNbtVal() {
            return nbtVal;
        }
        
        static Dimension getByNbtVal(int nbtVal) {
            for (Dimension cur : Dimension.values()) {
                if (cur.getNbtVal() == nbtVal) return cur;
            }
            
            return null;
        }
    }
    
    public static enum GameMode {
        // [GameType] [hardcore] in NBT
        CREATIVE_1_8(1, -1),
        SURVIVAL_1_8(0, -1),
        CREATIVE(1, 0),
        SURVIVAL(0, 0),
        HARDCORE(0, 1);

        private final int gameType;
        private final int hardcore;

        private GameMode(int gameType, int hardcore) {
            this.gameType = gameType;
            this.hardcore = hardcore;
        }

        Integer getGameType() {
            return gameType;
        }

        Byte getHardcore() {
            return hardcore == -1 ? null : (byte) hardcore;
        }
        
        private int getHardcoreScalar() {
            return hardcore;
        }
        
        static GameMode getByNbtVals(int gameType, Byte hardcore) {
            int scalarHardcore;
            if (hardcore == null)
                scalarHardcore = -1;
            else
                scalarHardcore = hardcore.intValue();
                        
            for (GameMode cur : GameMode.values()) {
                if (cur.getGameType().intValue() == gameType && cur.getHardcoreScalar() == scalarHardcore) {
                    return cur;
                }
            }
            
            return null;
        }
    }
    
    public static class LastPlayedComparator implements Comparator<GuiLevel> {
        public int compare(GuiLevel gl1, GuiLevel gl2) {
            return Long.signum(gl2.LastPlayed - gl1.LastPlayed);
        }
    }
}
