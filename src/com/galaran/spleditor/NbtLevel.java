package com.galaran.spleditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraftwiki.nbt.Tag;

class NbtLevel {
    private final Tag rootTag;

    // direct references
    private final Tag lastPlayedTag; // long
    
    private final Tag nameTag; // String
    private final Tag seedTag; // long

    private final Tag dimensionTag; // int
    private final Tag posXTag; // double
    private final Tag posYTag; // double
    private final Tag posZTag; // double

    private final Tag spawnXTag; // int
    private final Tag spawnYTag; // int
    private final Tag spawnZTag; // int

    private final Tag timeTag; // long
    private final Tag rainingTag; // byte
    private final Tag thunderingTag; // byte
    private final Tag rainTimeTag; // int
    private final Tag thunderTimeTag; // int

    private final Tag hpTag; // short
    private final Tag foodTag; // int
    private final Tag levelTag; // int

    private final Tag gameTypeTag; // int
    private final Tag hardcoreTag; // byte, null allowed

    NbtLevel(File levelDir) throws Exception {
        rootTag = Tag.readFrom(new FileInputStream(new File(levelDir, "level.dat")));
        if (rootTag == null) throw new IOException("Error reading root tag for level " + levelDir);

        Tag data = rootTag.getTagByName("Data");
        Tag player = data.getTagByName("Player");

        lastPlayedTag = data.getTagByName("LastPlayed");
        
        nameTag = data.getTagByName("LevelName");
        seedTag = data.getTagByName("RandomSeed");

        dimensionTag = data.getTagByName("Dimension");
        Tag[] playerCoords = (Tag[]) player.getTagByName("Pos").getValue();
        posXTag = playerCoords[0];
        posYTag = playerCoords[1];
        posZTag = playerCoords[2];

        spawnXTag = data.getTagByName("SpawnX");
        spawnYTag = data.getTagByName("SpawnY");
        spawnZTag = data.getTagByName("SpawnZ");

        timeTag = data.getTagByName("Time");
        rainingTag = data.getTagByName("raining");
        thunderingTag = data.getTagByName("thundering");
        rainTimeTag = data.getTagByName("rainTime");
        thunderTimeTag = data.getTagByName("thunderTime");

        hpTag = player.getTagByName("Health");
        foodTag = player.getTagByName("foodLevel");
        levelTag = player.getTagByName("XpLevel");

        gameTypeTag = data.getTagByName("GameType");
        hardcoreTag = data.findTagByName("hardcore"); // null for 1.8 beta level
    }
       
    void mergeWith(GuiLevel lev) {
        dimensionTag.setValue(lev.dimension.getNbtVal());
        
        // if position in nbt was not changed keep old, not replace with GUI integer
        if ( ((int) getPosX()) != lev.posX ) {
            posXTag.setValue(new Double(lev.posX));
        }
        if ( ((int) getPosY()) != lev.posY ) {
            posYTag.setValue(new Double(lev.posY));
        }
        if ( ((int) getPosZ()) != lev.posZ ) {
            posZTag.setValue(new Double(lev.posZ));
        }
        
        spawnXTag.setValue(new Integer(lev.spawnX));
        spawnYTag.setValue(new Integer(lev.spawnY));
        spawnZTag.setValue(new Integer(lev.spawnZ));
        
        timeTag.setValue(new Long(lev.time));
        rainingTag.setValue(lev.raining ? new Byte((byte)1) : new Byte((byte)0)); // java is so safe
        thunderingTag.setValue(lev.thundering ? new Byte((byte)1) : new Byte((byte)0));
        rainTimeTag.setValue(new Integer(lev.rainTime));
        thunderTimeTag.setValue(new Integer(lev.thunderTime));

        hpTag.setValue(new Short(lev.hp));
        foodTag.setValue(new Integer(lev.food));
        levelTag.setValue(new Integer(lev.level));

        gameTypeTag.setValue(lev.gameMode.getGameType());
        if (lev.gameMode.getHardcore() != null) {  // null for 1.8 beta level
            hardcoreTag.setValue(lev.gameMode.getHardcore());
        }
    }
    
    void save(File levDir) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(levDir, "level.dat"));
        rootTag.writeTo(fos);
        fos.close();
    }

    long getLastPlayed() {
        return (Long) lastPlayedTag.getValue();
    }
    
    String getName() {
        return (String) nameTag.getValue();
    }
    
    long getSeed() {
        return (Long) seedTag.getValue();
    }
    
    int getDimension() {
        return (Integer) dimensionTag.getValue();
    }
    
    double getPosX() {
        return (Double) posXTag.getValue();
    }

    double getPosY() {
        return (Double) posYTag.getValue();
    }

    double getPosZ() {
        return (Double) posZTag.getValue();
    }
    
    int getSpawnX() {
        return (Integer) spawnXTag.getValue();
    }

    int getSpawnY() {
        return (Integer) spawnYTag.getValue();
    }

    int getSpawnZ() {
        return (Integer) spawnZTag.getValue();
    }
    
    long getTime() {
        return (Long) timeTag.getValue();
    }
    
    byte getRaining() {
        return (Byte) rainingTag.getValue();
    }
    
    byte getThundering() {
        return (Byte) thunderingTag.getValue();
    }
    
    int getRainTime() {
        return (Integer) rainTimeTag.getValue();
    }

    int getThunderTime() {
        return (Integer) thunderTimeTag.getValue();
    }

    short getHp() {
        return (Short) hpTag.getValue();
    }
    
    int getFood() {
        return (Integer) foodTag.getValue();
    }

    int getLevel() {
        return (Integer) levelTag.getValue();
    }

    int getGameType() {
        return (Integer) gameTypeTag.getValue();
    }

    Byte getHardcore() {
        if (hardcoreTag == null) return null;
        return (Byte) hardcoreTag.getValue();
    }
}
