/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.bittorrent.websearch.monova;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.limewire.util.FilenameUtils;

import com.frostwire.bittorrent.websearch.WebSearchResult;
import com.frostwire.util.HtmlManipulator;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class MonovaWebSearchResult implements WebSearchResult {

    private String fileName;
    private String displayName;
    private String torrentDetailsURI;
    private String torrentURI;
    private String infoHash;
    private long size;
    private long creationTime;
    private int seeds;

    private final static long[] BYTE_MULTIPLIERS = new long[] { 1, 2 << 9, 2 << 19, 2 << 29, 2 << 39, 2 << 49 };

    private static final Map<String, Integer> UNIT_TO_BYTE_MULTIPLIERS_MAP;

    static {
        UNIT_TO_BYTE_MULTIPLIERS_MAP = new HashMap<String, Integer>();
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("B", 0);
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("KB", 1);
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("MB", 2);
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("GB", 3);
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("TB", 4);
        UNIT_TO_BYTE_MULTIPLIERS_MAP.put("PB", 5);
    }

    public MonovaWebSearchResult(String torrentDetailsUrl, Matcher matcher) {
        /*
         * Matcher groups cheatsheet
         * 1 -> .torrent URL
         * 2 -> infoHash
         * 3 -> seeds
         * 4 -> SIZE (B|KiB|MiBGiB)
         */
        this.torrentDetailsURI = torrentDetailsUrl;
        torrentURI = matcher.group(1);
        fileName = FilenameUtils.getName(torrentURI);
        displayName = HtmlManipulator.replaceHtmlEntities(fileName);
        infoHash = matcher.group(2).split("&")[0];
        creationTime = parseCreationTime(torrentURI);
        size = parseSize(matcher.group(4));
        seeds = parseSeeds(matcher.group(3));

        // Monova can't handle direct download of torrents without some sort of cookie
        // torrentURI = "magnet:?xt=urn:btih:" + infoHash;
    }

    private long parseSize(String group) {
        String[] size = group.trim().split(" ");
        String amount = size[0].trim();
        String unit = size[1].trim();

        long multiplier = BYTE_MULTIPLIERS[UNIT_TO_BYTE_MULTIPLIERS_MAP.get(unit)];

        //fractional size
        if (amount.indexOf(".") > 0) {
            float floatAmount = Float.parseFloat(amount);
            return (long) (floatAmount * multiplier);
        }
        //integer based size
        else {
            int intAmount = Integer.parseInt(amount);
            return (long) (intAmount * multiplier);
        }
    }

    private int parseSeeds(String group) {
        try {
            return Integer.parseInt(group);
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseCreationTime(String torrentDetailsUrl) {

        String[] arr = torrentDetailsUrl.split("/");
        arr = arr[6].split("-");

        int year = Integer.parseInt(arr[0]);
        int month = Integer.parseInt(arr[1]);
        int date = Integer.parseInt(arr[2]);

        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(year, month, date);
        return instance.getTimeInMillis();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getSource() {
        return "Monova";
    }

    @Override
    public String getHash() {
        return infoHash;
    }

    @Override
    public String getTorrentURI() {
        return torrentURI;
    }

    @Override
    public int getSeeds() {
        return seeds;
    }

    @Override
    public String getDetailsUrl() {
        return torrentDetailsURI;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
