/**
 * TravelMate Main Application Script
 * Implements 100% Reliable Non-Blank PDF Generation, Printable Window, Auth Integration, and Interactive Controls
 */
const App = {
  state: {
    attractions: [],
    selectedIds: [6, 8, 4], // Pre-seed 3 top places (Kalutara Bodhiya, Richmond Castle, Wadduwa Beach)
    currentCategory: 'All',
    searchQuery: '',
    availableHours: 8.0,
    startTime: '08:30',
    transportMode: 'car',
    includeLunch: true,
    optimizeRoute: true,
    currentPlan: null,
    theme: localStorage.getItem('travelmate_theme') || 'light'
  },

  async init() {
    this.applyTheme(this.state.theme);
    this.bindEvents();
    await this.loadCategories();
    await this.loadAttractions();
    this.renderFeaturedSlider();
    this.renderAboutGallery();
    MapManager.init();
    MapManager.renderAttractionMarkers(this.state.attractions, this.state.selectedIds);
    this.updateItineraryUI();
    Auth.updateNavbarAuth();
  },

  bindEvents() {
    // Theme toggle button
    const themeBtn = document.getElementById('themeToggleBtn');
    if (themeBtn) {
      themeBtn.addEventListener('click', () => this.toggleTheme());
    }

    // Search input with debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        this.state.searchQuery = e.target.value;
        this.debounce(() => this.filterAndRenderAttractions(), 250)();
      });
    }

    // Available time slider
    const timeInput = document.getElementById('availableHoursInput');
    if (timeInput) {
      timeInput.addEventListener('input', (e) => {
        this.state.availableHours = parseFloat(e.target.value) || 8.0;
        const display = document.getElementById('availableHoursDisplay');
        if (display) display.innerText = `${this.state.availableHours} hrs`;
        this.updateTimeBudgetPreview();
      });
    }

    // Start time input
    const startInput = document.getElementById('startTimeInput');
    if (startInput) {
      startInput.addEventListener('change', (e) => {
        this.state.startTime = e.target.value;
      });
    }

    // Transport Mode
    const transportSelect = document.getElementById('transportModeSelect');
    if (transportSelect) {
      transportSelect.addEventListener('change', (e) => {
        this.state.transportMode = e.target.value;
        this.updateTimeBudgetPreview();
      });
    }

    // Lunch Break
    const lunchCheck = document.getElementById('includeLunchCheck');
    if (lunchCheck) {
      lunchCheck.addEventListener('change', (e) => {
        this.state.includeLunch = e.target.checked;
        this.updateTimeBudgetPreview();
      });
    }

    // Optimize Route
    const optimizeCheckbox = document.getElementById('optimizeRouteCheck');
    if (optimizeCheckbox) {
      optimizeCheckbox.addEventListener('change', (e) => {
        this.state.optimizeRoute = e.target.checked;
      });
    }
  },

  // ==========================================
  // Theme Management
  // ==========================================
  toggleTheme() {
    const newTheme = this.state.theme === 'dark' ? 'light' : 'dark';
    this.state.theme = newTheme;
    localStorage.setItem('travelmate_theme', newTheme);
    this.applyTheme(newTheme);
    this.showToast(`Switched to ${newTheme === 'dark' ? 'Dark' : 'Light'} Mode`, 'info');
  },

  applyTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    const themeIcon = document.getElementById('themeToggleIcon');
    if (themeIcon) {
      themeIcon.className = theme === 'dark' ? 'bi bi-sun-fill text-warning' : 'bi bi-moon-stars-fill text-warning';
    }
  },

  // ==========================================
  // Featured Slider with Side Arrows
  // ==========================================
  renderFeaturedSlider() {
    const track = document.getElementById('featuredSliderTrack');
    if (!track || this.state.attractions.length === 0) return;

    const featured = this.state.attractions.slice(0, 6);
    track.innerHTML = featured.map(item => `
      <div class="featured-slide-item">
        <div class="attraction-card h-100 shadow-sm">
          <div class="card-img-wrapper" style="height: 160px;">
            <img src="${item.image}" alt="${item.name}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80'">
            <span class="category-tag">${item.category}</span>
            <span class="rating-tag">★ 4.8</span>
            <span class="distance-tag">~${item.distance} km</span>
          </div>
          <div class="p-3 d-flex flex-column flex-grow-1">
            <h6 class="fw-bold mb-1 text-truncate" title="${item.name}">${item.name}</h6>
            <small class="text-muted mb-2"><i class="bi bi-clock"></i> ${item.visitingDuration}h &bull; ${item.location.split(',')[0]}</small>
            <div class="mt-auto d-flex gap-1">
              <button class="btn btn-outline-primary btn-sm flex-grow-1 py-1" onclick="App.openAttractionDetails(${item.id})">
                <i class="bi bi-eye"></i> Details
              </button>
              <button class="btn btn-sm ${this.state.selectedIds.includes(Number(item.id)) ? 'btn-danger' : 'btn-primary'} py-1" onclick="App.toggleItinerary(${item.id})">
                ${this.state.selectedIds.includes(Number(item.id)) ? '<i class="bi bi-dash"></i>' : '<i class="bi bi-plus"></i>'}
              </button>
            </div>
          </div>
        </div>
      </div>
    `).join('');
  },

  scrollFeaturedSlider(direction) {
    const track = document.getElementById('featuredSliderTrack');
    if (!track) return;
    const scrollAmount = 320;
    if (direction === 'left') {
      track.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      track.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  },

  // ==========================================
  // About Page Places Gallery
  // ==========================================
  renderAboutGallery() {
    const galleryContainer = document.getElementById('aboutPlacesGallery');
    if (!galleryContainer || this.state.attractions.length === 0) return;

    galleryContainer.innerHTML = this.state.attractions.map(item => `
      <div class="col-6 col-md-4 col-lg-3 mb-3">
        <div class="card h-100 border rounded-3 overflow-hidden shadow-sm">
          <img src="${item.image}" alt="${item.name}" style="height: 120px; object-fit: cover;" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80'">
          <div class="p-2">
            <h6 class="mb-0 fw-bold small text-truncate" title="${item.name}">${item.name}</h6>
            <small class="text-muted d-block" style="font-size: 0.75rem;">${item.category} &bull; ~${item.distance}km</small>
          </div>
        </div>
      </div>
    `).join('');
  },

  // ==========================================
  // Categories & Filtering
  // ==========================================
  async loadCategories() {
    const categories = await ApiService.getCategories();
    const container = document.getElementById('categoryPillsContainer');
    if (!container) return;

    container.innerHTML = categories.map(cat => `
      <button type="button" class="category-pill ${cat === this.state.currentCategory ? 'active' : ''}" 
              onclick="App.selectCategory('${cat}', this)">
        ${this.getCategoryIcon(cat)} ${cat}
      </button>
    `).join('');
  },

  async loadAttractions() {
    this.renderLoading(true);
    this.state.attractions = await ApiService.getAttractions();
    this.renderAttractionsGrid(this.state.attractions);
    this.renderAboutGallery();
    this.renderLoading(false);
  },

  async selectCategory(category, element) {
    this.state.currentCategory = category;
    document.querySelectorAll('.category-pill').forEach(el => el.classList.remove('active'));
    if (element) {
      element.classList.add('active');
    } else {
      document.querySelectorAll('.category-pill').forEach(btn => {
        if (btn.innerText.includes(category)) btn.classList.add('active');
      });
    }
    await this.filterAndRenderAttractions();
  },

  async filterAndRenderAttractions() {
    const filtered = await ApiService.getAttractions(this.state.currentCategory, this.state.searchQuery);
    this.renderAttractionsGrid(filtered);
    MapManager.renderAttractionMarkers(filtered, this.state.selectedIds);
  },

  renderAttractionsGrid(list) {
    const container = document.getElementById('attractionsGrid');
    const emptyState = document.getElementById('emptyStateContainer');
    const countBadge = document.getElementById('attractionCountBadge');

    if (countBadge) {
      countBadge.innerText = `${list.length} attraction${list.length === 1 ? '' : 's'} available`;
    }

    if (!list || list.length === 0) {
      if (container) container.innerHTML = '';
      if (emptyState) emptyState.classList.remove('d-none');
      return;
    }

    if (emptyState) emptyState.classList.add('d-none');

    container.innerHTML = list.map(item => {
      const isSelected = this.state.selectedIds.includes(Number(item.id));

      return `
        <div class="col-md-6 col-lg-4 mb-4">
          <div class="attraction-card h-100">
            <div class="card-img-wrapper">
              <img src="${item.image}" alt="${item.name}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80'">
              <span class="category-tag">${item.category}</span>
              <span class="rating-tag">★ 4.8</span>
              <span class="distance-tag"><i class="bi bi-geo-alt-fill"></i> ~${item.distance} km</span>
            </div>
            <div class="card-body">
              <h5 class="attraction-title">${item.name}</h5>
              <div class="d-flex justify-content-between text-muted small mb-2">
                <span><i class="bi bi-clock-history text-primary"></i> Visit: <strong>${item.visitingDuration} hrs</strong></span>
                <span><i class="bi bi-pin-map text-secondary"></i> ${item.location.split(',')[0]}</span>
              </div>
              <p class="attraction-description">${item.description}</p>
              <div class="mt-auto pt-3 border-top d-flex gap-2">
                <button type="button" class="btn btn-outline-secondary btn-sm flex-grow-1" onclick="App.openAttractionDetails(${item.id})">
                  <i class="bi bi-info-circle"></i> Details
                </button>
                <button type="button" class="btn btn-sm ${isSelected ? 'btn-danger' : 'btn-primary'} px-3" 
                        onclick="App.toggleItinerary(${item.id})">
                  ${isSelected ? '<i class="bi bi-dash-circle"></i> Remove' : '<i class="bi bi-plus-circle"></i> Add'}
                </button>
              </div>
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  // ==========================================
  // Itinerary Basket & Reordering
  // ==========================================
  toggleItinerary(id) {
    const numId = Number(id);
    const index = this.state.selectedIds.indexOf(numId);
    if (index > -1) {
      this.state.selectedIds.splice(index, 1);
      this.showToast('Removed from itinerary', 'info');
    } else {
      this.state.selectedIds.push(numId);
      this.showToast('Added to itinerary!', 'success');
    }
    this.updateItineraryUI();
    this.filterAndRenderAttractions();
    this.renderFeaturedSlider();
  },

  moveItineraryItemUp(index) {
    if (index <= 0) return;
    const temp = this.state.selectedIds[index];
    this.state.selectedIds[index] = this.state.selectedIds[index - 1];
    this.state.selectedIds[index - 1] = temp;
    this.updateItineraryUI();
  },

  moveItineraryItemDown(index) {
    if (index >= this.state.selectedIds.length - 1) return;
    const temp = this.state.selectedIds[index];
    this.state.selectedIds[index] = this.state.selectedIds[index + 1];
    this.state.selectedIds[index + 1] = temp;
    this.updateItineraryUI();
  },

  loadPreset(presetName) {
    const presetMap = {
      'coastal_heritage': [6, 8, 4, 9], // Kalutara Bodhiya, Richmond Castle, Wadduwa Beach, Calido Beach
      'adventure_fun': [2, 1, 3],       // SL Karting, Pearl Bay, Bolgoda Lake
      'nature_trail': [3, 10, 9],       // Bolgoda Lake, Thudugala Waterfall, Calido Beach
      'express_4h': [2, 1]              // Karting, Pearl Bay
    };

    const ids = presetMap[presetName];
    if (ids) {
      this.state.selectedIds = [...ids];
      this.showToast(`Loaded ${presetName.replace(/_/g, ' ')} preset tour!`, 'success');
      this.updateItineraryUI();
      this.filterAndRenderAttractions();
      this.renderFeaturedSlider();
    }
  },

  updateItineraryUI() {
    const count = this.state.selectedIds.length;
    const floatingBadge = document.getElementById('floatingItineraryCount');
    const drawerBadge = document.getElementById('drawerItineraryCount');
    const panelCountBadge = document.getElementById('panelItineraryCount');
    const selectedContainer = document.getElementById('selectedPlacesList');
    const drawerContainer = document.getElementById('drawerPlacesList');

    if (floatingBadge) floatingBadge.innerText = count;
    if (drawerBadge) drawerBadge.innerText = count;
    if (panelCountBadge) panelCountBadge.innerText = `${count} selected`;

    const map = new Map(this.state.attractions.map(a => [Number(a.id), a]));
    const selectedList = this.state.selectedIds.map(id => map.get(id)).filter(Boolean);

    const emptyHtml = `
      <div class="text-center py-4 text-muted">
        <i class="bi bi-basket3 fs-1 text-secondary opacity-50 d-block mb-2"></i>
        <h6 class="fw-semibold">Itinerary is empty</h6>
        <small class="d-block mb-3">Add attractions or pick a recommended preset tour below:</small>
        <div class="d-flex flex-wrap gap-1 justify-content-center">
          <button type="button" class="preset-pill" onclick="App.loadPreset('coastal_heritage')">🏖️ Coastal & Heritage</button>
          <button type="button" class="preset-pill" onclick="App.loadPreset('adventure_fun')">🏎️ Adventure & Fun</button>
          <button type="button" class="preset-pill" onclick="App.loadPreset('nature_trail')">🌿 Nature & Waterfalls</button>
        </div>
      </div>
    `;

    const generateBtn = document.getElementById('generatePlanBtn');
    if (generateBtn) generateBtn.disabled = (count === 0);

    if (count === 0) {
      if (selectedContainer) selectedContainer.innerHTML = emptyHtml;
      if (drawerContainer) drawerContainer.innerHTML = emptyHtml;
      this.updateTimeBudgetPreview();
      return;
    }

    const itemsHtml = selectedList.map((item, idx) => `
      <div class="selected-item-row">
        <div class="d-flex align-items-center gap-2 overflow-hidden">
          <span class="badge bg-primary rounded-pill">${idx + 1}</span>
          <div class="text-truncate">
            <h6 class="mb-0 fw-semibold text-truncate" style="font-size: 0.92rem;">${item.name}</h6>
            <small class="text-muted"><i class="bi bi-clock"></i> ${item.visitingDuration}h &bull; ~${item.distance} km</small>
          </div>
        </div>
        <div class="d-flex align-items-center gap-1 flex-shrink-0">
          <button type="button" class="item-reorder-btn" title="Move Up" onclick="App.moveItineraryItemUp(${idx})" ${idx === 0 ? 'disabled style="opacity:0.3;"' : ''}>
            <i class="bi bi-arrow-up-circle-fill fs-6"></i>
          </button>
          <button type="button" class="item-reorder-btn" title="Move Down" onclick="App.moveItineraryItemDown(${idx})" ${idx === selectedList.length - 1 ? 'disabled style="opacity:0.3;"' : ''}>
            <i class="bi bi-arrow-down-circle-fill fs-6"></i>
          </button>
          <button type="button" class="item-reorder-btn text-danger ms-1" title="Remove" onclick="App.toggleItinerary(${item.id})">
            <i class="bi bi-x-circle-fill fs-6"></i>
          </button>
        </div>
      </div>
    `).join('');

    if (selectedContainer) selectedContainer.innerHTML = itemsHtml;
    if (drawerContainer) drawerContainer.innerHTML = itemsHtml;

    this.updateTimeBudgetPreview();
  },

  updateTimeBudgetPreview() {
    const map = new Map(this.state.attractions.map(a => [Number(a.id), a]));
    const selectedList = this.state.selectedIds.map(id => map.get(id)).filter(Boolean);

    let totalVisitTime = selectedList.reduce((acc, curr) => acc + (curr.visitingDuration || 0), 0);
    if (this.state.includeLunch && selectedList.length > 1) {
      totalVisitTime += 0.75;
    }

    const travelFactor = this.state.transportMode === 'tuktuk' ? 0.6 : 0.4;
    const totalTravelTime = selectedList.length * travelFactor;
    const totalEstimate = totalVisitTime + totalTravelTime;
    const available = this.state.availableHours || 8.0;

    const progressBar = document.getElementById('timeBudgetProgressBar');
    const budgetStatusText = document.getElementById('budgetStatusText');

    if (progressBar && budgetStatusText) {
      const percentage = Math.min(100, Math.round((totalEstimate / available) * 100));
      progressBar.style.width = `${percentage}%`;

      if (totalEstimate > available) {
        progressBar.className = 'progress-bar bg-danger';
        const over = (totalEstimate - available).toFixed(1);
        budgetStatusText.innerHTML = `
          <span class="text-danger fw-bold">
            <i class="bi bi-exclamation-triangle-fill"></i> Warning: ~${totalEstimate.toFixed(1)}h estimated (${over}h over your ${available}h limit). Consider removing a stop.
          </span>
        `;
      } else {
        progressBar.className = 'progress-bar bg-success';
        const left = (available - totalEstimate).toFixed(1);
        budgetStatusText.innerHTML = `
          <span class="text-success fw-semibold">
            <i class="bi bi-check-circle-fill"></i> Fits comfortably (~${totalEstimate.toFixed(1)}h / ${available}h with ${left}h buffer remaining).
          </span>
        `;
      }
    }
  },

  // ==========================================
  // Generate & Render Itinerary Schedule
  // ==========================================
  async generateItinerary() {
    if (this.state.selectedIds.length === 0) {
      this.state.selectedIds = [6, 8, 4];
      this.updateItineraryUI();
      this.showToast('Pre-selected top highlights around Atulugama for your day trip!', 'info');
    }

    const generateBtn = document.getElementById('generatePlanBtn');
    if (generateBtn) {
      generateBtn.disabled = true;
      generateBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status"></span> Calculating Best Route...`;
    }

    const requestPayload = {
      attractionIds: this.state.selectedIds,
      availableHours: this.state.availableHours,
      startTime: this.state.startTime,
      transportMode: this.state.transportMode,
      includeLunch: this.state.includeLunch,
      optimizeRoute: this.state.optimizeRoute,
      startLocationName: 'Atulugama, Kalutara',
      startLatitude: 6.7167,
      startLongitude: 80.0333
    };

    try {
      const plan = await ApiService.generateItineraryPlan(requestPayload);
      this.state.currentPlan = plan;
      this.renderItineraryResultModal(plan);
      try {
        MapManager.drawRoutePolyline(plan.stops);
      } catch (mapErr) {
        console.warn('Map polyline note:', mapErr);
      }
    } catch (err) {
      console.error('Plan error:', err);
      // Fallback directly to client side calculation
      const fallbackPlan = ApiService.calculateClientSideItinerary(requestPayload);
      this.state.currentPlan = fallbackPlan;
      this.renderItineraryResultModal(fallbackPlan);
    } finally {
      if (generateBtn) {
        generateBtn.disabled = false;
        generateBtn.innerHTML = `<i class="bi bi-lightning-charge-fill"></i> Generate Optimized Day Plan`;
      }
    }
  },

  renderItineraryResultModal(plan) {
    const modalContent = document.getElementById('itineraryResultModalBody');
    if (!modalContent) return;

    modalContent.innerHTML = `
      <div id="printableItineraryTemplate">
        
        <!-- Header Print Branding -->
        <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3">
          <div>
            <h3 class="fw-bold text-primary mb-0"><i class="bi bi-compass-fill"></i> TravelMate Day-Visit Itinerary</h3>
            <small class="text-muted">Starting Point: <strong>${plan.startLocation}</strong> &bull; Kalutara District, Sri Lanka</small>
          </div>
          <div class="text-end">
            <span class="badge bg-primary fs-6">${plan.startTime} - ${plan.estimatedEndTime}</span>
            <div class="text-muted small mt-1">Total Duration: <strong>${plan.totalEstimatedHours} hours</strong></div>
          </div>
        </div>

        <!-- Banner status -->
        <div class="alert ${plan.isExceedingTime ? 'alert-danger' : 'alert-success'} d-flex align-items-center mb-4 border no-print">
          <i class="bi ${plan.isExceedingTime ? 'bi-exclamation-triangle-fill fs-2' : 'bi-check-circle-fill fs-2'} me-3 flex-shrink-0"></i>
          <div>
            <h5 class="alert-heading mb-1 fw-bold">${plan.isExceedingTime ? 'Daily Time Limit Exceeded' : 'Optimal Day Plan Created!'}</h5>
            <p class="mb-0 small">${plan.statusMessage}</p>
          </div>
        </div>

        <!-- Metrics Overview -->
        <div class="row g-3 mb-4">
          <div class="col-6 col-md-3">
            <div class="p-3 bg-light rounded-3 text-center border">
              <span class="text-muted d-block small">Tour Window</span>
              <strong class="text-primary fs-6">${plan.startTime} - ${plan.estimatedEndTime}</strong>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="p-3 bg-light rounded-3 text-center border">
              <span class="text-muted d-block small">Total Tour Time</span>
              <strong class="text-dark fs-6">${plan.totalEstimatedHours} hours</strong>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="p-3 bg-light rounded-3 text-center border">
              <span class="text-muted d-block small">Visiting Duration</span>
              <strong class="text-secondary fs-6">${plan.totalVisitingHours} hours</strong>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="p-3 bg-light rounded-3 text-center border">
              <span class="text-muted d-block small">Total Driving</span>
              <strong class="text-info fs-6">~${plan.totalTravelDistanceKm} km (${plan.totalTravelHours}h)</strong>
            </div>
          </div>
        </div>

        <!-- Step-by-Step Timeline Schedule -->
        <h6 class="fw-bold mb-3"><i class="bi bi-calendar3-range text-primary"></i> Step-by-Step Day Schedule:</h6>
        <div class="timeline">
          <!-- Start point -->
          <div class="timeline-item">
            <div class="timeline-dot start-dot"><i class="bi bi-house-fill" style="font-size: 11px;"></i></div>
            <div class="timeline-card">
              <div class="d-flex justify-content-between align-items-center">
                <strong class="text-success"><i class="bi bi-flag-fill me-1"></i> ${plan.startTime} &bull; Depart Starting Point</strong>
                <span class="badge bg-success">Start</span>
              </div>
              <small class="text-muted">Origin: ${plan.startLocation}</small>
            </div>
          </div>

          <!-- Stops -->
          ${plan.stops.map((stop, idx) => `
            <div class="timeline-item">
              <div class="timeline-dot"><span>${stop.stopOrder}</span></div>
              <div class="travel-step-badge">
                <i class="bi bi-car-front-fill text-primary"></i> ${stop.travelNote}
              </div>
              <div class="timeline-card mt-2">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                  <div>
                    <h6 class="fw-bold mb-1 text-primary">${stop.stopOrder}. ${stop.attraction.name}</h6>
                    <span class="badge bg-light text-dark border me-1">${stop.attraction.category}</span>
                    <small class="text-muted"><i class="bi bi-geo-alt"></i> ${stop.attraction.location}</small>
                  </div>
                  <div class="text-end">
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle fw-semibold px-2 py-1">
                      <i class="bi bi-clock"></i> ${stop.arrivalTime} - ${stop.departureTime}
                    </span>
                    <div class="text-muted small mt-1">Visiting Time: ${stop.visitingDurationHours}h</div>
                  </div>
                </div>
              </div>
            </div>
          `).join('')}

          <!-- Conclude -->
          <div class="timeline-item">
            <div class="timeline-dot finish-dot"><i class="bi bi-check-lg" style="font-size: 12px;"></i></div>
            <div class="timeline-card">
              <div class="d-flex justify-content-between align-items-center">
                <strong><i class="bi bi-sunset-fill text-warning me-1"></i> ${plan.estimatedEndTime} &bull; Tour Concluded</strong>
                <span class="badge bg-dark">End</span>
              </div>
              <small class="text-muted">Return to Atulugama or proceed to evening dinner.</small>
            </div>
          </div>
        </div>

        <!-- Tips and Safety Disclaimers (NFR-006 to NFR-009) -->
        <div class="p-3 bg-light rounded-3 border mt-4">
          <h6 class="fw-bold mb-2"><i class="bi bi-info-circle-fill text-primary"></i> Important Visitor Advice & Disclaimers:</h6>
          <ul class="mb-0 ps-3 small text-secondary">
            ${plan.tipsAndSafetyNotes.map(t => `<li class="mb-1">${t}</li>`).join('')}
          </ul>
        </div>
      </div>
    `;

    const gmapsBtn = document.getElementById('openGoogleMapsRouteBtn');
    if (gmapsBtn) {
      gmapsBtn.href = plan.googleMapsDirectionsUrl;
    }

    const modal = new bootstrap.Modal(document.getElementById('itineraryResultModal'));
    modal.show();
  },

  // ==========================================
  // 100% Reliable Non-Blank PDF Generator
  // ==========================================
  downloadItineraryPDF() {
    if (!this.state.currentPlan) {
      this.showToast('No active itinerary generated yet.', 'warning');
      return;
    }

    const plan = this.state.currentPlan;
    this.showToast('Generating official TravelMate PDF itinerary...', 'info');

    // Construct a standalone, pristine HTML element with explicit inline styling
    const pdfDiv = document.createElement('div');
    pdfDiv.style.width = '750px';
    pdfDiv.style.padding = '30px';
    pdfDiv.style.background = '#ffffff';
    pdfDiv.style.color = '#111827';
    pdfDiv.style.fontFamily = 'Arial, Helvetica, sans-serif';
    pdfDiv.style.position = 'fixed';
    pdfDiv.style.left = '-9999px';
    pdfDiv.style.top = '0';

    let stopsHtml = '';
    plan.stops.forEach((s, idx) => {
      stopsHtml += `
        <div style="border-left: 3px solid #0284c7; padding-left: 15px; margin-bottom: 18px;">
          <div style="font-size: 11px; color: #64748b; margin-bottom: 3px;">${s.travelNote}</div>
          <div style="font-size: 15px; font-weight: bold; color: #0284c7; margin-bottom: 2px;">
            ${s.stopOrder}. ${s.attraction.name}
          </div>
          <div style="font-size: 12px; color: #374151; margin-bottom: 4px;">
            <strong>Category:</strong> ${s.attraction.category} &bull; <strong>Location:</strong> ${s.attraction.location}
          </div>
          <div style="font-size: 12px; background: #e0f2fe; padding: 4px 8px; border-radius: 4px; display: inline-block; font-weight: bold; color: #0369a1;">
            Time Slot: ${s.arrivalTime} - ${s.departureTime} (${s.visitingDurationHours} hours visiting)
          </div>
        </div>
      `;
    });

    let tipsHtml = '';
    plan.tipsAndSafetyNotes.forEach(t => {
      tipsHtml += `<li style="font-size: 11px; color: #475569; margin-bottom: 4px;">${t}</li>`;
    });

    pdfDiv.innerHTML = `
      <div style="border-bottom: 2px solid #0284c7; padding-bottom: 15px; margin-bottom: 20px;">
        <h1 style="color: #0284c7; margin: 0 0 5px 0; font-size: 24px; font-weight: bold;">
          TravelMate – Smart Tourist Day-Visit Itinerary
        </h1>
        <p style="margin: 0; font-size: 12px; color: #64748b;">
          Hub: <strong>${plan.startLocation}</strong> (25 km Planning Area, Kalutara District, Western Province, Sri Lanka)
        </p>
      </div>

      <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 12px; margin-bottom: 20px; display: flex; justify-content: space-between;">
        <div>
          <div style="font-size: 11px; color: #64748b;">Tour Window:</div>
          <div style="font-size: 14px; font-weight: bold; color: #0284c7;">${plan.startTime} - ${plan.estimatedEndTime}</div>
        </div>
        <div>
          <div style="font-size: 11px; color: #64748b;">Total Duration:</div>
          <div style="font-size: 14px; font-weight: bold;">${plan.totalEstimatedHours} hours</div>
        </div>
        <div>
          <div style="font-size: 11px; color: #64748b;">Visiting Time:</div>
          <div style="font-size: 14px; font-weight: bold;">${plan.totalVisitingHours} hours</div>
        </div>
        <div>
          <div style="font-size: 11px; color: #64748b;">Total Driving:</div>
          <div style="font-size: 14px; font-weight: bold;">~${plan.totalTravelDistanceKm} km (${plan.totalTravelHours}h)</div>
        </div>
      </div>

      <h3 style="font-size: 16px; font-weight: bold; color: #1e293b; margin: 0 0 15px 0; border-bottom: 1px solid #cbd5e1; padding-bottom: 5px;">
        Step-by-Step Day Schedule:
      </h3>

      <div style="margin-bottom: 10px;">
        <div style="font-size: 13px; font-weight: bold; color: #16a34a; margin-bottom: 15px;">
          📍 ${plan.startTime} &bull; Depart Starting Point (${plan.startLocation})
        </div>
        ${stopsHtml}
        <div style="font-size: 13px; font-weight: bold; color: #0f172a; margin-top: 15px;">
          🏁 ${plan.estimatedEndTime} &bull; Tour Concluded &bull; Return / Evening Dinner
        </div>
      </div>

      <div style="background: #f1f5f9; border-radius: 6px; padding: 12px; margin-top: 25px;">
        <h4 style="margin: 0 0 6px 0; font-size: 12px; font-weight: bold; color: #0f172a;">
          Important Visitor Notes & Disclaimers:
        </h4>
        <ul style="margin: 0; padding-left: 18px;">
          ${tipsHtml}
        </ul>
      </div>

      <div style="margin-top: 25px; font-size: 10px; color: #94a3b8; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 10px;">
        Generated by TravelMate &bull; University of Moratuwa ITE2953 Project &bull; Rukshana Roshan (E2410132)
      </div>
    `;

    document.body.appendChild(pdfDiv);

    const opt = {
      margin:       8,
      filename:     'TravelMate-Day-Itinerary.pdf',
      image:        { type: 'jpeg', quality: 0.98 },
      html2canvas:  { scale: 2, useCORS: true, logging: false },
      jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    if (window.html2pdf) {
      html2pdf().set(opt).from(pdfDiv).save().then(() => {
        pdfDiv.remove();
        this.showToast('Itinerary PDF downloaded successfully!', 'success');
      }).catch((err) => {
        console.error('PDF generation error:', err);
        pdfDiv.remove();
        this.printItinerary();
      });
    } else {
      pdfDiv.remove();
      this.printItinerary();
    }
  },

  // ==========================================
  // Direct High-Fidelity Printable Window
  // ==========================================
  printItinerary() {
    if (!this.state.currentPlan) {
      this.showToast('No active itinerary to print.', 'warning');
      return;
    }

    const plan = this.state.currentPlan;
    const printWindow = window.open('', '_blank', 'width=800,height=900');
    if (!printWindow) {
      window.print();
      return;
    }

    let stopsHtml = '';
    plan.stops.forEach(s => {
      stopsHtml += `
        <div style="border-left: 3px solid #0284c7; padding-left: 12px; margin-bottom: 16px;">
          <div style="font-size: 11px; color: #666;">${s.travelNote}</div>
          <div style="font-size: 15px; font-weight: bold; color: #0284c7;">${s.stopOrder}. ${s.attraction.name}</div>
          <div style="font-size: 12px; color: #333;">Category: ${s.attraction.category} &bull; Location: ${s.attraction.location}</div>
          <div style="font-size: 12px; font-weight: bold; color: #0369a1; background: #e0f2fe; display: inline-block; padding: 2px 6px; border-radius: 4px; margin-top: 4px;">
            Schedule: ${s.arrivalTime} - ${s.departureTime} (Visiting: ${s.visitingDurationHours}h)
          </div>
        </div>
      `;
    });

    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>TravelMate Day Itinerary - ${plan.startLocation}</title>
        <style>
          body { font-family: Arial, sans-serif; padding: 25px; color: #111; line-height: 1.4; }
          h1 { color: #0284c7; margin-bottom: 4px; }
          .summary-box { background: #f8fafc; border: 1px solid #ddd; border-radius: 6px; padding: 12px; margin: 15px 0; display: flex; justify-content: space-between; }
        </style>
      </head>
      <body>
        <h1>TravelMate – Smart Tourist Day-Visit Itinerary</h1>
        <p style="color: #666; font-size: 13px; margin-top: 0;">Hub: ${plan.startLocation} &bull; Kalutara District, Sri Lanka</p>
        
        <div class="summary-box">
          <div><strong>Tour Time:</strong><br>${plan.startTime} - ${plan.estimatedEndTime}</div>
          <div><strong>Total Duration:</strong><br>${plan.totalEstimatedHours} hours</div>
          <div><strong>Visiting Time:</strong><br>${plan.totalVisitingHours} hours</div>
          <div><strong>Driving:</strong><br>~${plan.totalTravelDistanceKm} km (${plan.totalTravelHours}h)</div>
        </div>

        <h3>Step-by-Step Day Schedule:</h3>
        <p style="color: #16a34a; font-weight: bold;">📍 ${plan.startTime} &bull; Depart Starting Point (${plan.startLocation})</p>
        ${stopsHtml}
        <p style="color: #0f172a; font-weight: bold;">🏁 ${plan.estimatedEndTime} &bull; Tour Concluded</p>

        <div style="background: #f1f5f9; padding: 10px; border-radius: 6px; margin-top: 25px; font-size: 11px;">
          <strong>Travel Notes & Disclaimers:</strong>
          <ul>
            ${plan.tipsAndSafetyNotes.map(t => `<li>${t}</li>`).join('')}
          </ul>
        </div>
      </body>
      </html>
    `);

    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
      printWindow.print();
    }, 500);
  },

  copyItineraryText() {
    if (!this.state.currentPlan) return;
    const plan = this.state.currentPlan;
    let text = `🗓️ TravelMate One-Day Itinerary (${plan.startLocation})\n`;
    text += `⏰ Schedule: ${plan.startTime} to ${plan.estimatedEndTime} (${plan.totalEstimatedHours} hours total)\n\n`;
    plan.stops.forEach(s => {
      text += `📍 Stop ${s.stopOrder}: ${s.attraction.name}\n   - Arrive: ${s.arrivalTime} | Depart: ${s.departureTime} (Duration: ${s.visitingDurationHours}h)\n   - ${s.travelNote}\n\n`;
    });
    text += `🗺️ Google Maps Directions: ${plan.googleMapsDirectionsUrl}`;

    navigator.clipboard.writeText(text).then(() => {
      this.showToast('Itinerary copied to clipboard for sharing!', 'success');
    }).catch(() => {
      this.showToast('Could not copy automatically, please copy manually.', 'warning');
    });
  },

  saveCurrentPlanToAccount() {
    if (!this.state.currentPlan) {
      this.showToast('No active plan generated yet', 'warning');
      return;
    }
    const user = Auth.getCurrentUser();
    if (!user) {
      this.showToast('Please sign in or register to save your plan to your account', 'warning');
      setTimeout(() => { window.location.href = 'login.html'; }, 1500);
      return;
    }
    try {
      Auth.savePlanForCurrentUser(this.state.currentPlan);
      this.showToast('Plan saved to your TravelMate account!', 'success');
    } catch (err) {
      this.showToast(err.message, 'danger');
    }
  },

  openSavedPlansModal() {
    const plans = Auth.getSavedPlansForCurrentUser();
    const modalBody = document.getElementById('savedPlansModalBody');
    if (!modalBody) return;

    if (plans.length === 0) {
      modalBody.innerHTML = `
        <div class="text-center py-4 text-muted">
          <i class="bi bi-journal-x fs-1 text-secondary opacity-50 d-block mb-2"></i>
          <h6>No Saved Itineraries</h6>
          <small>Generate an itinerary in the planner and click "Save to Account" to store it here.</small>
        </div>
      `;
    } else {
      modalBody.innerHTML = plans.map(item => `
        <div class="p-3 bg-light rounded-3 border mb-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <strong class="text-primary fs-6">${item.plan.totalAttractionsCount} Stops &bull; ${item.plan.totalEstimatedHours}h Tour</strong>
            <small class="text-muted">${item.createdAt}</small>
          </div>
          <p class="small text-secondary mb-2">
            Stops: ${item.plan.stops.map(s => s.attraction.name).join(' &rarr; ')}
          </p>
          <div class="d-flex justify-content-between align-items-center">
            <button class="btn btn-primary btn-sm" onclick="App.renderItineraryResultModal(${JSON.stringify(item.plan).replace(/"/g, '&quot;')}); bootstrap.Modal.getInstance(document.getElementById('savedPlansModal')).hide();">
              <i class="bi bi-eye"></i> View Full Plan
            </button>
            <button class="btn btn-outline-danger btn-sm" onclick="Auth.deleteSavedPlan(${item.id}); App.openSavedPlansModal();">
              <i class="bi bi-trash"></i> Delete
            </button>
          </div>
        </div>
      `).join('');
    }

    const modal = new bootstrap.Modal(document.getElementById('savedPlansModal'));
    modal.show();
  },

  // ==========================================
  // Attraction Detail Modal
  // ==========================================
  async openAttractionDetails(id) {
    const attraction = await ApiService.getAttractionById(id);
    if (!attraction) return;

    const isSelected = this.state.selectedIds.includes(Number(attraction.id));
    const modalBody = document.getElementById('attractionModalBody');
    const modalTitle = document.getElementById('attractionModalTitle');
    const modalActionBtn = document.getElementById('modalItineraryToggleBtn');

    if (modalTitle) modalTitle.innerText = attraction.name;

    if (modalBody) {
      modalBody.innerHTML = `
        <div class="row g-4">
          <div class="col-md-6">
            <img src="${attraction.image}" class="img-fluid rounded-3 shadow-sm w-100" style="height: 280px; object-fit: cover;" alt="${attraction.name}" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80'">
          </div>
          <div class="col-md-6">
            <div class="d-flex gap-2 mb-3 flex-wrap">
              <span class="badge bg-primary">${attraction.category}</span>
              <span class="badge bg-warning text-dark">★ 4.8 Rating</span>
              <span class="badge bg-light text-dark border">~${attraction.distance} km from Atulugama</span>
            </div>
            <p class="text-secondary small leading-relaxed mb-3">${attraction.description}</p>
            
            <ul class="list-group list-group-flush mb-3 small">
              <li class="list-group-item px-0 d-flex justify-content-between bg-transparent">
                <span class="text-muted"><i class="bi bi-clock text-primary"></i> Visiting Duration:</span>
                <strong>${attraction.visitingDuration} hours</strong>
              </li>
              <li class="list-group-item px-0 d-flex justify-content-between bg-transparent">
                <span class="text-muted"><i class="bi bi-geo-alt text-danger"></i> Location:</span>
                <strong>${attraction.location}</strong>
              </li>
              ${attraction.bestTime ? `
              <li class="list-group-item px-0 d-flex justify-content-between bg-transparent">
                <span class="text-muted"><i class="bi bi-sun text-warning"></i> Best Visiting Time:</span>
                <strong>${attraction.bestTime}</strong>
              </li>` : ''}
              ${attraction.entryFee ? `
              <li class="list-group-item px-0 d-flex justify-content-between bg-transparent">
                <span class="text-muted"><i class="bi bi-ticket-perforated text-success"></i> Entry / Tickets:</span>
                <strong>${attraction.entryFee}</strong>
              </li>` : ''}
            </ul>

            <div class="d-flex gap-2">
              <a href="${attraction.mapLink || '#'}" target="_blank" class="btn btn-outline-primary btn-sm flex-grow-1">
                <i class="bi bi-map-fill"></i> Google Maps
              </a>
              <button type="button" class="btn btn-sm btn-outline-secondary" onclick="MapManager.centerOn(${attraction.latitude}, ${attraction.longitude}); bootstrap.Modal.getInstance(document.getElementById('attractionDetailModal')).hide();">
                <i class="bi bi-crosshair"></i> Show on Map
              </button>
            </div>
          </div>
        </div>
      `;
    }

    if (modalActionBtn) {
      modalActionBtn.className = `btn ${isSelected ? 'btn-danger' : 'btn-primary'} btn-sm`;
      modalActionBtn.innerHTML = isSelected 
        ? '<i class="bi bi-dash-circle"></i> Remove from Itinerary' 
        : '<i class="bi bi-plus-circle"></i> Add to Itinerary';
      modalActionBtn.onclick = () => {
        this.toggleItinerary(attraction.id);
        const modal = bootstrap.Modal.getInstance(document.getElementById('attractionDetailModal'));
        if (modal) modal.hide();
      };
    }

    const modal = new bootstrap.Modal(document.getElementById('attractionDetailModal'));
    modal.show();
  },

  showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-white bg-${type === 'warning' ? 'warning text-dark' : type === 'danger' ? 'danger' : type === 'success' ? 'success' : 'primary'} border-0`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');

    toastEl.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">
          ${message}
        </div>
        <button type="button" class="btn-close ${type === 'warning' ? '' : 'btn-close-white'} me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    `;

    container.appendChild(toastEl);
    const toast = new bootstrap.Toast(toastEl, { delay: 2800 });
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
  },

  renderLoading(isLoading) {
    const spinner = document.getElementById('loadingSpinner');
    if (spinner) {
      spinner.style.display = isLoading ? 'block' : 'none';
    }
  },

  getCategoryIcon(category) {
    const map = {
      'All': '<i class="bi bi-grid-fill"></i>',
      'Recreation': '<i class="bi bi-water"></i>',
      'Adventure': '<i class="bi bi-speedometer2"></i>',
      'Nature / Scenic': '<i class="bi bi-tree-fill"></i>',
      'Beach': '<i class="bi bi-umbrella-fill"></i>',
      'Beach / Nature': '<i class="bi bi-tsunami"></i>',
      'Religious / Cultural': '<i class="bi bi-building"></i>',
      'Historical': '<i class="bi bi-bank"></i>',
      'Nature / Waterfall': '<i class="bi bi-moisture"></i>'
    };
    return map[category] || '<i class="bi bi-tag-fill"></i>';
  },

  debounce(func, wait) {
    let timeout;
    return (...args) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(this, args), wait);
    };
  }
};

document.addEventListener('DOMContentLoaded', () => App.init());
