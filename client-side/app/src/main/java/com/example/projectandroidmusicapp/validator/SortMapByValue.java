package com.example.projectandroidmusicapp.validator;

import com.example.projectandroidmusicapp.entity.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class SortMapByValue {

    public static LinkedHashMap<String, Song> sortHashMapByValues(
            HashMap<String, Song> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Song> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        for (Song song : mapValues){
            System.out.println(song.getTitle() + " - " + song.getUrlSong());
        }

        LinkedHashMap<String, Song> sortedMap = new LinkedHashMap<>();

        Iterator<Song> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Song val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Song comp1 = passedMap.get(key);
                Song comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key,val);
                    break;
                }
            }
        }
        mapKeys = new ArrayList<>(sortedMap.keySet());
        mapValues = new ArrayList<>(sortedMap.values());

        for (Song song : mapValues){
            System.out.println(song.getTitle() + " - " + song.getUrlSong());
        }
        return sortedMap;
    }
}
