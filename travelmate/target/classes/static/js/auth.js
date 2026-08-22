/**
 * TravelMate Authentication & User Management Module
 */
const Auth = {
  // Demo users stored in localStorage or default
  getUsers() {
    const saved = localStorage.getItem('travelmate_users');
    if (saved) return JSON.parse(saved);
    const defaults = [
      {
        id: 1,
        name: 'Rukshana Roshan',
        email: 'tourist@travelmate.lk',
        password: 'password123',
        role: 'tourist',
        savedPlans: []
      },
      {
        id: 2,
        name: 'Administrator',
        email: 'admin@travelmate.lk',
        password: 'adminpassword',
        role: 'admin',
        savedPlans: []
      }
    ];
    localStorage.setItem('travelmate_users', JSON.stringify(defaults));
    return defaults;
  },

  getCurrentUser() {
    const userJson = localStorage.getItem('travelmate_current_user');
    return userJson ? JSON.parse(userJson) : null;
  },

  login(email, password) {
    const users = this.getUsers();
    const cleanEmail = (email || '').trim().toLowerCase();
    const user = users.find(u => u.email.toLowerCase() === cleanEmail && u.password === password);
    if (!user) {
      throw new Error('Invalid email or password. Please try again.');
    }
    const sessionUser = {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role
    };
    localStorage.setItem('travelmate_current_user', JSON.stringify(sessionUser));
    return sessionUser;
  },

  register(name, email, password, role = 'tourist') {
    const users = this.getUsers();
    const cleanEmail = (email || '').trim().toLowerCase();
    if (users.some(u => u.email.toLowerCase() === cleanEmail)) {
      throw new Error('An account with this email already exists. Please log in.');
    }
    const newUser = {
      id: Date.now(),
      name: name.trim(),
      email: cleanEmail,
      password: password,
      role: role,
      savedPlans: []
    };
    users.push(newUser);
    localStorage.setItem('travelmate_users', JSON.stringify(users));

    const sessionUser = {
      id: newUser.id,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role
    };
    localStorage.setItem('travelmate_current_user', JSON.stringify(sessionUser));
    return sessionUser;
  },

  logout() {
    localStorage.removeItem('travelmate_current_user');
    window.location.href = 'index.html';
  },

  savePlanForCurrentUser(plan) {
    const currentUser = this.getCurrentUser();
    if (!currentUser) {
      throw new Error('Please log in or register to save your day itineraries.');
    }
    const plansKey = `travelmate_saved_plans_${currentUser.email}`;
    const existing = JSON.parse(localStorage.getItem(plansKey) || '[]');
    const newEntry = {
      id: Date.now(),
      createdAt: new Date().toLocaleString(),
      plan: plan
    };
    existing.unshift(newEntry);
    localStorage.setItem(plansKey, JSON.stringify(existing));
    return existing;
  },

  getSavedPlansForCurrentUser() {
    const currentUser = this.getCurrentUser();
    if (!currentUser) return [];
    const plansKey = `travelmate_saved_plans_${currentUser.email}`;
    return JSON.parse(localStorage.getItem(plansKey) || '[]');
  },

  deleteSavedPlan(planId) {
    const currentUser = this.getCurrentUser();
    if (!currentUser) return;
    const plansKey = `travelmate_saved_plans_${currentUser.email}`;
    let existing = JSON.parse(localStorage.getItem(plansKey) || '[]');
    existing = existing.filter(p => p.id !== planId);
    localStorage.setItem(plansKey, JSON.stringify(existing));
    return existing;
  },

  updateNavbarAuth() {
    const authContainer = document.getElementById('navbarAuthContainer');
    if (!authContainer) return;

    const user = this.getCurrentUser();
    if (user) {
      const isAdmin = user.role === 'admin';
      authContainer.innerHTML = `
        <div class="dropdown">
          <button class="btn btn-outline-light btn-sm dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle text-primary fs-6"></i>
            <span>${user.name.split(' ')[0]}</span>
            <span class="badge ${isAdmin ? 'bg-danger' : 'bg-success'} rounded-pill" style="font-size: 0.65rem;">${isAdmin ? 'Admin' : 'Tourist'}</span>
          </button>
          <ul class="dropdown-menu dropdown-menu-end shadow-lg border">
            <li class="dropdown-header text-muted small">Signed in as<br><strong class="text-dark">${user.email}</strong></li>
            <li><hr class="dropdown-divider"></li>
            <li>
              <a class="dropdown-item" href="#" onclick="App.openSavedPlansModal(); return false;">
                <i class="bi bi-journal-bookmark-fill text-primary me-2"></i> My Saved Itineraries
              </a>
            </li>
            ${isAdmin ? `
            <li>
              <a class="dropdown-item" href="admin.html">
                <i class="bi bi-speedometer2 text-danger me-2"></i> Admin CRUD Dashboard
              </a>
            </li>` : ''}
            <li><hr class="dropdown-divider"></li>
            <li>
              <a class="dropdown-item text-danger" href="#" onclick="Auth.logout(); return false;">
                <i class="bi bi-box-arrow-right me-2"></i> Sign Out
              </a>
            </li>
          </ul>
        </div>
      `;
    } else {
      authContainer.innerHTML = `
        <a href="login.html" class="btn btn-outline-light btn-sm px-3 fw-semibold">
          <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
        </a>
        <a href="register.html" class="btn btn-primary btn-sm px-3 fw-semibold">
          Register
        </a>
      `;
    }
  }
};

document.addEventListener('DOMContentLoaded', () => Auth.updateNavbarAuth());
