package com.github.sirblobman.combatlogx.api.assists;

import java.util.Map;
import java.util.UUID;

public interface IAssistManager {
  Map<UUID, Double> getDamagedBy(UUID player);

  double getTotalDamage(UUID player);

  double getDamage(UUID damagee, UUID damager);

  void damage(UUID damagee, UUID damager, Double amount);

  int getAssists(UUID player);

  void setAssists(UUID player, int assists);
}
