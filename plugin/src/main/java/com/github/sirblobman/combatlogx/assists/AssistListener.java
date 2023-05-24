package com.github.sirblobman.combatlogx.assists;

import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class AssistListener extends CombatListener {
  private final AssistManager manager;

  public AssistListener(@NotNull ICombatLogX plugin, AssistManager manager) {
    super(plugin);
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Player damager)) return;
    if(!(event.getEntity() instanceof Player damagee)) return;

    manager.damage(damagee.getUniqueId(), damager.getUniqueId(), event.getFinalDamage());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDeath(PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final double total = manager.getTotalDamage(player.getUniqueId());
    final Map<UUID, Double> damages = manager.getDamagedBy(player.getUniqueId());
    damages.forEach((damager, amt) -> {
      final Player dmgr = Bukkit.getPlayer(damager);
      if(dmgr == null) return;
      int percentage = (int) ((amt / total) * 100);

      getJavaPlugin().getLanguageManager().sendMessage(dmgr, "assist_kill", new StringReplacer("{enemy}", player.getDisplayName()), new StringReplacer("{percentage}", String.valueOf(percentage)));
    });
    damages.clear();
  }
}
