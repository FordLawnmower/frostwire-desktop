package com.frostwire.bittorrent.websearch.btjunkie;

import java.util.List;

/**
{ "results": [
 {
  "title": "Shakira - She Wolf CDRip 320Kbps [2009][Cov+CD][Bubanee]",
   "date": "Sat, 10 Oct 2009 00:00:00 +0000",
   "peers": 131,
   "seeds": 630,
   "category": "Audio",
   "cdp": "http://btjunkie.org/torrent/Shakira-She-Wolf-CDRip-320Kbps-2009-Cov-CD-Bubanee/4196c99eddf4fbb1d4939b4a87868f2269f148c227e7",
   "comments": 83,
   "size": 99614720,
   "votes": 999,
   "download": "http://dl.btjunkie.org/torrent/Shakira-She-Wolf-CDRip-320Kbps-2009-Cov-CD-Bubanee/4196c99eddf4fbb1d4939b4a87868f2269f148c227e7/download.torrent",
   "hash": "c99eddf4fbb1d4939b4a87868f2269f148c227e7"
 },
*/
public class BTJunkieResponse {
    public List<BTJunkieItem> results;
}