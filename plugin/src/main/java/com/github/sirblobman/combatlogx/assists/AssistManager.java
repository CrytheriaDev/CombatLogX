package com.github.sirblobman.combatlogx.assists;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.assists.IAssistManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssistManager implements IAssistManager {
  private final Map<UUID, Map<UUID, Double>> damages = new HashMap<>();
  private final Map<UUID, Integer> assists = new HashMap<>();
  private final ICombatLogX plugin;

  public AssistManager(ICombatLogX plugin) {
    this.plugin = plugin;
  }

  public Map<UUID, Double> getDamagedBy(UUID player) {
    damages.putIfAbsent(player, new HashMap<>());

    return damages.get(player);
  }

  public double getTotalDamage(UUID player) {
    return getDamagedBy(player).values().stream().mapToDouble(x -> x).sum();
  }

  public double getDamage(UUID damagee, UUID damager) {
    final Map<UUID, Double> damage = getDamagedBy(damagee);
    damage.putIfAbsent(damager, 0.0);
    return damage.get(damager);
  }

  public void damage(UUID damagee, UUID damager, Double amount) {
    getDamagedBy(damagee).merge(damager, amount, Double::sum);
  }

  @Override
  public int getAssists(UUID player) {
    if(!assists.containsKey(player)) {
      loadPlayerAssists(player);
    }
    return assists.getOrDefault(player, 0);
  }

  private void loadPlayerAssists(UUID player) {
    final int assists = plugin.getPlayerDataManager().get(Bukkit.getOfflinePlayer(player)).getInt("assists", 0);
    this.assists.put(player, assists);
  }

  @Override
  public void setAssists(UUID player, int assists) {
    this.assists.put(player, Math.max(0, assists));
    final YamlConfiguration loaded = plugin.getPlayerDataManager().get(Bukkit.getOfflinePlayer(player));
    loaded.set("assists", this.assists.get(player));
    plugin.getPlayerDataManager().save(Bukkit.getOfflinePlayer(player));
  }
}
