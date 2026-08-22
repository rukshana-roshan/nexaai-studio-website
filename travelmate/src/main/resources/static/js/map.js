/**
 * TravelMate Leaflet & Map Controller
 */
const MapManager = {
  map: null,
  markersLayer: null,
  routeLayer: null,
  radiusCircle: null,
  atulugamaCoords: [6.7167, 80.0333],

  init() {
    const mapElement = document.getElementById('mapContainer');
    if (!mapElement) return;

    // Check if Leaflet L is loaded
    if (typeof L === 'undefined') {
      mapElement.innerHTML = `
        <div class="d-flex align-items-center justify-content-center h-100 text-muted">
          <p><i class="bi bi-geo-alt-fill text-danger fs-3"></i><br>Map service loading...</p>
        </div>`;
      return;
    }

    // Initialize Leaflet Map centered at Atulugama
    this.map = L.map('mapContainer').setView(this.atulugamaCoords, 11);

    // Add OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    // Layer groups for dynamic markers and route
    this.markersLayer = L.layerGroup().addTo(this.map);
    this.routeLayer = L.layerGroup().addTo(this.map);

    // Add Atulugama center marker
    const homeIcon = L.divIcon({
      className: 'custom-div-icon',
      html: `<div style="background:#10b981; color:white; border-radius:50%; width:32px; height:32px; display:flex; align-items:center; justify-content:center; border:2px solid white; box-shadow:0 2px 6px rgba(0,0,0,0.3); font-weight:bold;"><i class="bi bi-house-door-fill"></i></div>`,
      iconSize: [32, 32],
      iconAnchor: [16, 16]
    });

    L.marker(this.atulugamaCoords, { icon: homeIcon })
      .bindPopup(`<strong>📍 Starting Point: Atulugama</strong><br><small class="text-muted">Kalutara District, Western Province</small>`)
      .addTo(this.map);

    // Draw 25km radius circle
    this.radiusCircle = L.circle(this.atulugamaCoords, {
      radius: 25000, // 25 km
      color: '#0284c7',
      fillColor: '#0284c7',
      fillOpacity: 0.05,
      weight: 1.5,
      dashArray: '5, 5'
    }).addTo(this.map);
  },

  renderAttractionMarkers(attractions, selectedIds = []) {
    if (!this.map || !this.markersLayer) return;
    this.markersLayer.clearLayers();

    const categoryColors = {
      'recreation': '#0284c7',
      'adventure': '#ea580c',
      'nature / scenic': '#16a34a',
      'beach': '#0891b2',
      'religious / cultural': '#9333ea',
      'historical': '#b45309',
      'nature / waterfall': '#059669'
    };

    attractions.forEach(attr => {
      if (!attr.latitude || !attr.longitude) return;

      const catKey = (attr.category || '').toLowerCase();
      const color = categoryColors[catKey] || '#0284c7';
      const isSelected = selectedIds.includes(Number(attr.id));

      const markerIcon = L.divIcon({
        className: 'custom-pin-icon',
        html: `
          <div style="
            background: ${isSelected ? '#e11d48' : color};
            color: white;
            border-radius: 50%;
            width: ${isSelected ? '32px' : '26px'};
            height: ${isSelected ? '32px' : '26px'};
            display: flex;
            align-items: center;
            justify-content: center;
            border: 2px solid white;
            box-shadow: 0 3px 8px rgba(0,0,0,0.3);
            font-size: ${isSelected ? '14px' : '11px'};
            transition: all 0.2s ease;
          ">
            ${isSelected ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-geo-alt-fill"></i>'}
          </div>
        `,
        iconSize: isSelected ? [32, 32] : [26, 26],
        iconAnchor: isSelected ? [16, 16] : [13, 13]
      });

      const popupHtml = `
        <div style="max-width: 220px; font-family: inherit;">
          <img src="${attr.image}" style="width: 100%; height: 90px; object-fit: cover; border-radius: 6px; margin-bottom: 6px;" alt="${attr.name}">
          <h6 class="mb-1 fw-bold text-dark" style="font-size: 0.95rem;">${attr.name}</h6>
          <div class="mb-1">
            <span class="badge" style="background: ${color}; font-size: 0.7rem;">${attr.category}</span>
            <span class="badge bg-light text-dark border ms-1" style="font-size: 0.7rem;">~${attr.distance} km</span>
          </div>
          <p class="text-muted mb-2" style="font-size: 0.78rem; line-height: 1.3;">${attr.description.substring(0, 75)}...</p>
          <div class="d-flex gap-1">
            <button class="btn btn-sm btn-primary py-1 px-2 flex-grow-1" style="font-size: 0.75rem;" onclick="App.openAttractionDetails(${attr.id})">
              <i class="bi bi-info-circle"></i> Details
            </button>
            <button class="btn btn-sm ${isSelected ? 'btn-danger' : 'btn-outline-primary'} py-1 px-2" style="font-size: 0.75rem;" onclick="App.toggleItinerary(${attr.id})">
              ${isSelected ? '<i class="bi bi-dash"></i>' : '<i class="bi bi-plus"></i>'}
            </button>
          </div>
        </div>
      `;

      const marker = L.marker([attr.latitude, attr.longitude], { icon: markerIcon })
        .bindPopup(popupHtml);

      this.markersLayer.addLayer(marker);
    });
  },

  drawRoutePolyline(stops) {
    if (!this.map || !this.routeLayer) return;
    this.routeLayer.clearLayers();

    if (!stops || stops.length === 0) return;

    const latlngs = [this.atulugamaCoords];
    stops.forEach(stop => {
      if (stop.attraction && stop.attraction.latitude && stop.attraction.longitude) {
        latlngs.push([stop.attraction.latitude, stop.attraction.longitude]);
      }
    });

    if (latlngs.length > 1) {
      const polyline = L.polyline(latlngs, {
        color: '#0284c7',
        weight: 4,
        opacity: 0.8,
        dashArray: '8, 8'
      }).addTo(this.routeLayer);

      this.map.fitBounds(polyline.getBounds(), { padding: [40, 40] });
    }
  },

  centerOn(lat, lng) {
    if (this.map && lat && lng) {
      this.map.setView([lat, lng], 14, { animate: true });
    }
  }
};
