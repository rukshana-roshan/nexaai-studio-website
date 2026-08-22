/**
 * TravelMate Node.js Full Development Server
 * Serves static frontend + implements full /api/attractions and /api/itinerary REST services
 */
const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const STATIC_DIR = path.join(__dirname, 'src', 'main', 'resources', 'static');

const MIME_TYPES = {
  '.html': 'text/html',
  '.css': 'text/css',
  '.js': 'text/javascript',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml'
};

// In-Memory Database of 10 Attractions around Atulugama
let attractionsData = [
  {
    id: 1,
    name: "Pearl Bay, Bandaragama",
    category: "Recreation",
    description: "A premier international leisure and aquatic theme park in Bandaragama featuring 4 distinct adventure zones, exhilarating multi-lane speed water slides, splash pads, lazy river lagoons, and family dining facilities.",
    image: "https://images.unsplash.com/photo-1575429198097-0414ec08e8cd?w=1000&auto=format&fit=crop&q=80",
    distance: 5.0,
    visitingDuration: 3.0,
    location: "Bandaragama, Kalutara District",
    latitude: 6.7205,
    longitude: 79.9880,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.7205,79.9880",
    bestTime: "10:00 AM - 04:30 PM",
    entryFee: "LKR 2,500 - 4,500"
  },
  {
    id: 2,
    name: "Sri Lanka Karting Circuit, Bandaragama",
    category: "Adventure",
    description: "The premier international karting and motorsport facility in South Asia with a 775-meter professional asphalt circuit, high-speed rental go-karts, safety gear, timing systems, and night racing floodlights.",
    image: "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=1000&auto=format&fit=crop&q=80",
    distance: 4.0,
    visitingDuration: 2.0,
    location: "Bandaragama, Kalutara District",
    latitude: 6.7289,
    longitude: 79.9922,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.7289,79.9922",
    bestTime: "02:00 PM - 08:00 PM",
    entryFee: "LKR 3,000 / 10-min session"
  },
  {
    id: 3,
    name: "Bolgoda Lake",
    category: "Nature / Scenic",
    description: "The largest natural freshwater lake in the Western Province, offering tranquil eco-tours, bird watching, boating, paddle-boarding, and serene water vistas fringed by lush mangroves.",
    image: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=1000&auto=format&fit=crop&q=80",
    distance: 6.5,
    visitingDuration: 2.0,
    location: "Bandaragama / Moratuwa border",
    latitude: 6.7450,
    longitude: 79.9480,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.7450,79.9480",
    bestTime: "07:00 AM - 10:00 AM or 04:00 PM - 06:30 PM",
    entryFee: "Free (Boat hire ~LKR 3,500/hr)"
  },
  {
    id: 4,
    name: "Wadduwa Beach",
    category: "Beach",
    description: "A picturesque golden coastal shoreline renowned for its sweeping palm-lined stretch, calming Indian Ocean surf, vibrant beach volleyball, and scenic coastal photography.",
    image: "https://images.unsplash.com/photo-1506953823976-52e1fdc0149a?w=1000&auto=format&fit=crop&q=80",
    distance: 14.0,
    visitingDuration: 2.0,
    location: "Wadduwa, Kalutara District",
    latitude: 6.6667,
    longitude: 79.9333,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.6667,79.9333",
    bestTime: "04:30 PM - 06:45 PM",
    entryFee: "Free admission"
  },
  {
    id: 5,
    name: "Pothupitiya Beach",
    category: "Beach",
    description: "A tranquil seaside haven located between Wadduwa and Kalutara, famous for uncrowded coastal strolls, traditional fishing catamaran scenery, and refreshing ocean breezes.",
    image: "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=1000&auto=format&fit=crop&q=80",
    distance: 15.0,
    visitingDuration: 1.5,
    location: "Pothupitiya, Wadduwa",
    latitude: 6.6380,
    longitude: 79.9410,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.6380,79.9410",
    bestTime: "05:00 PM - 06:30 PM",
    entryFee: "Free admission"
  },
  {
    id: 6,
    name: "Kalutara Bodhiya",
    category: "Religious / Cultural",
    description: "One of Sri Lanka's most revered sacred Buddhist pilgrimage sites, home to an ancient sacred Bo tree planted during the Anuradhapura era and the iconic giant white Chaitya stupa at the Kalutara Bridge over Kalu Ganga.",
    image: "https://images.unsplash.com/photo-1548013146-72479768bada?w=1000&auto=format&fit=crop&q=80",
    distance: 19.0,
    visitingDuration: 1.5,
    location: "Kalutara North, Kalutara District",
    latitude: 6.5878,
    longitude: 79.9602,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.5878,79.9602",
    bestTime: "06:00 AM - 09:00 AM or 05:00 PM - 07:30 PM",
    entryFee: "Free admission"
  },
  {
    id: 7,
    name: "Gangatilaka Viharaya",
    category: "Religious / Cultural",
    description: "Renowned for having the only completely hollow Buddhist stupa in the world, featuring 74 vivid mural paintings depicting the 550 Jataka tales and magnificent terrace river views.",
    image: "https://images.unsplash.com/photo-1598890777032-bde835ba27c2?w=1000&auto=format&fit=crop&q=80",
    distance: 19.2,
    visitingDuration: 1.5,
    location: "Kalutara South, Kalutara District",
    latitude: 6.5865,
    longitude: 79.9610,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.5865,79.9610",
    bestTime: "08:00 AM - 11:30 AM",
    entryFee: "Free admission"
  },
  {
    id: 8,
    name: "Richmond Castle",
    category: "Historical",
    description: "An opulent early 20th-century Edwardian mansion built between 1900 and 1910 across 42 acres, featuring teak woodwork from Burma, Italian stained glass, and historic European-style gardens.",
    image: "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=1000&auto=format&fit=crop&q=80",
    distance: 20.5,
    visitingDuration: 2.0,
    location: "Palatota, Kalutara",
    latitude: 6.5702,
    longitude: 79.9886,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.5702,79.9886",
    bestTime: "09:00 AM - 04:00 PM",
    entryFee: "LKR 100 (Locals) / LKR 500 (Tourists)"
  },
  {
    id: 9,
    name: "Calido Beach",
    category: "Beach / Nature",
    description: "A rare and breathtaking coastal land spit situated directly between the flowing Kalu Ganga river and the Indian Ocean, perfect for sunset watching, sea breeze walks, and bird photography.",
    image: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&auto=format&fit=crop&q=80",
    distance: 21.0,
    visitingDuration: 1.5,
    location: "Kalutara",
    latitude: 6.5815,
    longitude: 79.9530,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.5815,79.9530",
    bestTime: "04:30 PM - 06:30 PM",
    entryFee: "Free admission"
  },
  {
    id: 10,
    name: "Thudugala Ella",
    category: "Nature / Waterfall",
    description: "A refreshing 8-meter natural cascade nestled inside an old rubber and rainforest estate in Dodangoda, featuring cool natural plunge pools and lush canopy trails.",
    image: "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=1000&auto=format&fit=crop&q=80",
    distance: 22.5,
    visitingDuration: 2.5,
    location: "Thudugala, Dodangoda, Kalutara District",
    latitude: 6.6025,
    longitude: 80.0520,
    mapLink: "https://www.google.com/maps/search/?api=1&query=6.6025,80.0520",
    bestTime: "08:30 AM - 01:00 PM",
    entryFee: "Free admission"
  }
];

const server = http.createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const urlObj = new URL(req.url, `http://${req.headers.host}`);
  const pathname = urlObj.pathname;

  // REST API: GET /api/attractions
  if (pathname === '/api/attractions' && req.method === 'GET') {
    const category = urlObj.searchParams.get('category');
    const query = urlObj.searchParams.get('query');
    let results = [...attractionsData];

    if (category && category !== 'All') {
      results = results.filter(a => a.category.toLowerCase().includes(category.toLowerCase()));
    }
    if (query) {
      const q = query.toLowerCase();
      results = results.filter(a =>
        a.name.toLowerCase().includes(q) ||
        a.description.toLowerCase().includes(q) ||
        a.location.toLowerCase().includes(q)
      );
    }
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(results));
    return;
  }

  // REST API: GET /api/attractions/categories
  if (pathname === '/api/attractions/categories' && req.method === 'GET') {
    const categories = [
      'All',
      'Recreation',
      'Adventure',
      'Nature / Scenic',
      'Beach',
      'Religious / Cultural',
      'Historical',
      'Nature / Waterfall'
    ];
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(categories));
    return;
  }

  // REST API: POST /api/itinerary/plan
  if (pathname === '/api/itinerary/plan' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk.toString(); });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const map = new Map(attractionsData.map(a => [a.id, a]));
        let selected = (payload.attractionIds || []).map(id => map.get(Number(id))).filter(Boolean);

        if (payload.optimizeRoute) {
          selected.sort((a, b) => a.distance - b.distance);
        }

        let currentMinutes = 8 * 60 + 30; // 08:30
        if (payload.startTime) {
          const [h, m] = payload.startTime.split(':').map(Number);
          if (!isNaN(h) && !isNaN(m)) currentMinutes = h * 60 + m;
        }

        const formatTime = (mins) => {
          const h24 = Math.floor(mins / 60) % 24;
          const m = Math.floor(mins % 60);
          const h12 = h24 % 12 === 0 ? 12 : h24 % 12;
          const ampm = h24 < 12 ? 'AM' : 'PM';
          return `${String(h12).padStart(2, '0')}:${String(m).padStart(2, '0')} ${ampm}`;
        };

        const startTimeFormatted = formatTime(currentMinutes);
        let totalVisitingHours = 0;
        let totalTravelMinutes = 0;
        let totalDistanceKm = 0;
        const stops = [];

        const speedFactor = payload.transportMode === 'tuktuk' ? 24 : payload.transportMode === 'bike' ? 35 : 30;

        selected.forEach((item, index) => {
          const legDist = (index === 0) ? item.distance : Math.max(2.5, Math.abs(item.distance - selected[index - 1].distance));
          const travelMins = Math.max(8, Math.round((legDist / speedFactor) * 60) + 5);
          
          totalDistanceKm += legDist;
          totalTravelMinutes += travelMins;
          currentMinutes += travelMins;

          const arrivalTime = formatTime(currentMinutes);
          const visitMins = Math.round(item.visitingDuration * 60);
          totalVisitingHours += item.visitingDuration;
          currentMinutes += visitMins;
          const departureTime = formatTime(currentMinutes);

          stops.push({
            stopOrder: index + 1,
            attraction: item,
            arrivalTime,
            departureTime,
            visitingDurationHours: item.visitingDuration,
            travelTimeFromPreviousMinutes: travelMins,
            distanceFromPreviousKm: Math.round(legDist * 10) / 10,
            travelNote: (index === 0)
              ? `Depart Atulugama -> Drive ~${legDist.toFixed(1)} km (~${travelMins} mins)`
              : `Drive ~${legDist.toFixed(1)} km (~${travelMins} mins) to ${item.name}`
          });

          if (payload.includeLunch && index === 1) {
            currentMinutes += 45;
            totalVisitingHours += 0.75;
          }
        });

        const totalTravelHours = Math.round((totalTravelMinutes / 60) * 10) / 10;
        const totalEstimatedHours = Math.round((totalVisitingHours + totalTravelHours) * 10) / 10;
        const availableHours = payload.availableHours || 8.0;
        const isExceeding = totalEstimatedHours > availableHours;
        const diff = Math.round(Math.abs(totalEstimatedHours - availableHours) * 10) / 10;

        const plan = {
          startLocation: "Atulugama, Kalutara District",
          startTime: startTimeFormatted,
          estimatedEndTime: formatTime(currentMinutes),
          availableHours,
          totalVisitingHours: Math.round(totalVisitingHours * 10) / 10,
          totalTravelHours,
          totalEstimatedHours,
          totalTravelDistanceKm: Math.round(totalDistanceKm * 10) / 10,
          isExceedingTime: isExceeding,
          timeDifferenceHours: diff,
          totalAttractionsCount: stops.length,
          statusMessage: isExceeding
            ? `⚠️ Time Warning: Planned trip requires ${totalEstimatedHours}h (${totalVisitingHours}h visits + ${totalTravelHours}h travel), which exceeds your available ${availableHours}h budget by ${diff}h. Consider removing 1 attraction.`
            : `✅ Optimal Day Plan! Total time: ${totalEstimatedHours}h (${totalVisitingHours}h visits + ${totalTravelHours}h travel). You have ${diff}h buffer remaining in your day.`,
          stops,
          googleMapsDirectionsUrl: "https://www.google.com/maps/dir/Atulugama,+Sri+Lanka/" + selected.map(s => encodeURIComponent(s.name + ", " + s.location)).join("/"),
          tipsAndSafetyNotes: [
            "📌 Note (NFR-006 / BR-007): All distances and travel times are approximate and reflect typical Sri Lankan road conditions.",
            "🙏 Temple Etiquette: Modest attire covering shoulders and knees is mandatory when visiting sacred Buddhist places like Kalutara Bodhiya & Gangatilaka Viharaya.",
            "🌅 Beach Advice: Calido Beach and Wadduwa Beach offer the best views and cooler ocean breeze between 04:30 PM - 06:30 PM.",
            "👟 Waterfall Preparation: Wear sturdy non-slip footwear when visiting Thudugala Ella and exercise caution around rocky streams.",
            "⏱️ Peak Hours: Bandaragama-Kalutara road can experience mild traffic between 08:00 AM - 09:30 AM and 05:00 PM - 06:30 PM."
          ]
        };

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(plan));
      } catch (err) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: err.message }));
      }
    });
    return;
  }

  // Static File Serving
  let filePath = path.join(STATIC_DIR, pathname === '/' ? 'index.html' : pathname);
  const extname = String(path.extname(filePath)).toLowerCase();
  const contentType = MIME_TYPES[extname] || 'application/octet-stream';

  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === 'ENOENT') {
        res.writeHead(404, { 'Content-Type': 'text/html' });
        res.end('<h1>404 Not Found</h1>', 'utf-8');
      } else {
        res.writeHead(500);
        res.end('Server Error: ' + error.code, 'utf-8');
      }
    } else {
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content, 'utf-8');
    }
  });
});

server.listen(PORT, () => {
  console.log(`=================================================`);
  console.log(` TravelMate Full Server running at:`);
  console.log(` 👉 http://localhost:${PORT}`);
  console.log(` 👉 http://localhost:${PORT}/admin.html (Admin Portal)`);
  console.log(` 👉 REST APIs active on /api/attractions & /api/itinerary/plan`);
  console.log(`=================================================`);
});
