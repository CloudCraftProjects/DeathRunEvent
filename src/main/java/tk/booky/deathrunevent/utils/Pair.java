package tk.booky.deathrunevent.utils;
// Created by booky10 in DeathRunEvent (13:11 09.07.21)

public record Pair<K, V>(K key, V value) {

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
