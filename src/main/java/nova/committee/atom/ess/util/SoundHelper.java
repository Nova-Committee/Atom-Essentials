package nova.committee.atom.ess.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/20 21:36
 * Version: 1.0
 */
public class SoundHelper {
    /**
     * Plays a global sound that everyone in the Level can hear.
     *
     * @param level  The level to play in.
     * @param sound  The sound to play.
     * @param source The source of the sound.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    public static void playGlobal(Level level, SoundEvent sound, SoundSource source, float volume, float pitch) {

        for (Player player : level.players()) {
            playAtLocationLocal(new Location(player), sound, source, volume, pitch);
        }
    }

    /**
     * Plays a sound at a Location.
     *
     * @param location The Location to play the sound at.
     * @param sound    The sound to play.
     * @param source   The source of the sound.
     * @param volume   The volume of the sound.
     * @param pitch    The pitch of the sound.
     */
    public static void playAtLocation(Location location, SoundEvent sound, SoundSource source, float volume, float pitch) {
        location.level.playSound(null, location.getBlockPos(), sound, source, volume, pitch);
    }

    /**
     * Plays a sound at a Location on client-side:
     *
     * @param location The Location to play the sound at.
     * @param sound    The sound to play.
     * @param source   The source of the sound.
     * @param volume   The volume of the sound.
     * @param pitch    The pitch of the sound.
     */
    public static void playAtLocationLocal(Location location, SoundEvent sound, SoundSource source, float volume, float pitch) {
        location.level.playLocalSound(location.x, location.y, location.z, sound, source, volume, pitch, false);
    }

    /**
     * Plays a sound at a Player.
     *
     * @param player The Player to play the sound at.
     * @param sound  The sound to play.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    public static void playAtPlayer(Player player, SoundEvent sound, float volume, float pitch) {
        player.getLevel().playSound(player, new BlockPos(player.getBlockX(), player.getBlockY(), player.getZ()), sound, SoundSource.PLAYERS, volume, pitch);
    }

    /**
     * Plays a sound at a Player with:
     * SoundSource = PLAYERS
     * volume = 1
     * pitch = 1
     *
     * @param player The Player to play the sound at.
     * @param sound  The sound to play.
     */
    public static void playSimple(Player player, SoundEvent sound) {
        playAtPlayer(player, sound, 1, 1);
    }

    /**
     * Players a Block's placing sound at a Location.
     *
     * @param location The Location to play the sound at.
     * @param state    The BlockState of the placed Block.
     */
    public static void playBlockPlace(Location location, BlockState state) {
        playAtLocation(location, state.getSoundType(location.level, location.getBlockPos(), null).getPlaceSound(), SoundSource.BLOCKS, 1F, 1F);
    }

    /**
     * Players a Block's break sound at a Location.
     *
     * @param location The Location to play the sound at.
     * @param state    The BlockState of the broke Block.
     */
    public static void playBlockBreak(Location location, BlockState state) {
        playAtLocation(location, state.getSoundType(location.level, location.getBlockPos(), null).getBreakSound(), SoundSource.BLOCKS, 1F, 1F);
    }
}
