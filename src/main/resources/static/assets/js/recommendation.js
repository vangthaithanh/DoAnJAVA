document.addEventListener("DOMContentLoaded", () => {
  const root = document.getElementById("itineraryRecommendation");
  if (!root) return;

  const destination = document.getElementById("recommendDestination");
  const placeList = document.getElementById("recommendPlaceList");
  const serviceList = document.getElementById("recommendServiceList");
  const button = document.getElementById("recommendItineraryButton");
  const warning = document.getElementById("recommendationWarning");
  const status = document.getElementById("recommendationStatus");
  const results = document.getElementById("recommendationResults");
  const nextPlaces = document.getElementById("recommendNextPlaces");
  const nextServices = document.getElementById("recommendNextServices");
  const placeServices = document.getElementById("recommendPlaceServices");
  const tours = document.getElementById("recommendTours");
  const serviceLabels = new Map();
  const destinations = new Map();

  const escapeHtml = value => String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#039;");

  const emptyState = message => `<p class="recommendation-empty">${escapeHtml(message)}</p>`;
  const selectedValues = container => Array.from(container.querySelectorAll("input:checked"))
    .map(input => input.value);

  const readinessWarning = destinationInfo => destinationInfo?.modelReadiness === "not_ready"
    ? "Dữ liệu điểm đến này còn ít, kết quả chỉ mang tính tham khảo."
    : destinationInfo?.modelReadiness === "limited"
      ? "Dữ liệu điểm đến này còn hạn chế, vui lòng xem kết quả như thông tin tham khảo."
      : "";

  const showWarning = message => {
    warning.textContent = message;
    warning.hidden = !message;
  };

  const clearResults = () => {
    results.hidden = true;
    status.textContent = "";
  };

  const renderChips = (container, items, valueKey, labelKey) => {
    container.innerHTML = items.length ? items.map(item => `
      <label class="recommendation-place">
        <input type="checkbox" value="${escapeHtml(item[valueKey])}">
        <span>${escapeHtml(item[labelKey])}</span>
      </label>
    `).join("") : emptyState("Chưa có dữ liệu để lựa chọn.");
  };

  const loadDestinationCatalog = async () => {
    clearResults();
    serviceLabels.clear();
    showWarning(readinessWarning(destinations.get(destination.value)));
    placeList.innerHTML = "<span class=\"recommendation-status\">Đang tải danh sách địa điểm...</span>";
    serviceList.innerHTML = "<span class=\"recommendation-status\">Đang tải danh sách dịch vụ...</span>";
    try {
      const [placesResponse, servicesResponse] = await Promise.all([
        fetch(`/api/recommend/places?destinationKey=${encodeURIComponent(destination.value)}`),
        fetch(`/api/recommend/services?destinationKey=${encodeURIComponent(destination.value)}`)
      ]);
      if (!placesResponse.ok || !servicesResponse.ok) {
        throw new Error("Không tải được dữ liệu điểm đến.");
      }
      const [places, services] = await Promise.all([placesResponse.json(), servicesResponse.json()]);
      services.forEach(service => serviceLabels.set(service.serviceKey, service.serviceLabel));
      renderChips(placeList, places, "name", "name");
      renderChips(serviceList, services, "serviceKey", "serviceLabel");
    } catch (error) {
      placeList.innerHTML = emptyState(error.message);
      serviceList.innerHTML = emptyState(error.message);
    }
  };

  const loadDestinations = async () => {
    try {
      const response = await fetch("/api/recommend/destinations");
      if (!response.ok) throw new Error("Không tải được danh sách điểm đến.");
      const items = await response.json();
      items.forEach(item => destinations.set(item.destinationKey, item));
      destination.innerHTML = items.map(item => `
        <option value="${escapeHtml(item.destinationKey)}">${escapeHtml(item.destinationName)}</option>
      `).join("");
      destination.value = destinations.has("da_lat") ? "da_lat" : items[0]?.destinationKey ?? "";
      await loadDestinationCatalog();
    } catch (error) {
      status.textContent = error.message;
    }
  };

  const renderNextPlaces = response => {
    const items = response.recommendations ?? [];
    nextPlaces.innerHTML = items.length ? items.map(item => `
      <button class="recommendation-item recommendation-suggestion" type="button"
              data-recommend-kind="place" data-recommend-value="${escapeHtml(item.consequent)}">
        <span class="recommendation-place-title">
          <strong>${escapeHtml(item.consequent)}</strong>
          <span class="recommendation-level-badge ${item.recommendation_level === "tham_khao" ? "rare" : "core"}">
            ${item.recommendation_level === "tham_khao" ? "Tham khảo" : "Nên chọn"}
          </span>
        </span>
        <small>${escapeHtml(item.support_count)} lượt khớp${item.recommendation_level === "tham_khao" ? " · ít dữ liệu" : ""}</small>
        <span>+ Thêm vào tiêu chí</span>
      </button>
    `).join("") : emptyState("Chưa có gợi ý địa điểm phù hợp.");
  };

  const renderNextServices = response => {
    const items = response.recommendations ?? [];
    nextServices.innerHTML = items.length ? items.map(item => `
      <button class="recommendation-item recommendation-suggestion" type="button"
              data-recommend-kind="service" data-recommend-value="${escapeHtml(item.consequent_service)}">
        <span class="recommendation-place-title">
          <strong>${escapeHtml(serviceLabels.get(item.consequent_service) ?? item.consequent_service)}</strong>
          <span class="recommendation-level-badge ${item.recommendation_level === "tham_khao" ? "rare" : "core"}">
            ${item.recommendation_level === "tham_khao" ? "Tham khảo" : "Nên chọn"}
          </span>
        </span>
        <small>${escapeHtml(item.support_count)} lượt khớp</small>
        <span>+ Thêm vào tiêu chí</span>
      </button>
    `).join("") : emptyState("Chưa có gợi ý dịch vụ phù hợp.");
  };

  const renderPlaceServices = response => {
    const items = response.recommendations ?? [];
    placeServices.innerHTML = items.length ? items.map(item => `
      <button class="recommendation-item recommendation-suggestion" type="button"
              data-recommend-kind="service" data-recommend-value="${escapeHtml(item.service_key)}">
        <strong>${escapeHtml(item.service_label)}</strong>
        <small>${escapeHtml(item.service_available_count)}/${escapeHtml(item.service_known_count)} tour có dữ liệu · ${escapeHtml(item.confidence_level)}</small>
        <span>+ Thêm vào tiêu chí</span>
      </button>
    `).join("") : emptyState("Không còn dịch vụ theo địa điểm cần bổ sung.");
  };

  const renderTours = response => {
    const items = response.tours ?? [];
    tours.innerHTML = items.length ? items.map(item => `
      <div class="recommendation-item">
        <strong>${escapeHtml(item.tieu_de)}</strong>
        <small>${escapeHtml(item.so_ngay)} ngày · ${escapeHtml(item.nguon)} · ${escapeHtml(item.recommendationReason)}</small>
        ${item.detailUrl ? `
          <a class="recommendation-tour-link" href="${escapeHtml(item.detailUrl)}"
             ${item.bookable ? "" : "target=\"_blank\" rel=\"noopener noreferrer\""}>
            ${item.bookable ? "Xem tour" : "Xem nguồn tham khảo"}
          </a>
        ` : ""}
      </div>
    `).join("") : emptyState("Không có tour đáp ứng toàn bộ tiêu chí đã chọn.");
  };

  destination.addEventListener("change", loadDestinationCatalog);

  const submitRecommendations = async () => {
    const selectedPlaces = selectedValues(placeList);
    const selectedServices = selectedValues(serviceList);
    if (!selectedPlaces.length && !selectedServices.length) {
      status.textContent = "Vui lòng chọn ít nhất một địa điểm hoặc dịch vụ.";
      results.hidden = true;
      return;
    }

    button.disabled = true;
    status.textContent = "Đang tạo gợi ý từ model v11...";
    try {
      const response = await fetch("/api/recommend/full", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          destinationKey: destination.value,
          selectedPlaces,
          selectedServices,
          topK: 5
        })
      });
      if (!response.ok) throw new Error("API gợi ý chưa phản hồi thành công.");
      const data = await response.json();
      renderNextPlaces(data.nextPlaces);
      renderNextServices(data.nextServices);
      renderPlaceServices(data.placeServices);
      renderTours(data.recommendedTours);
      showWarning((data.warnings ?? []).join(" ") || readinessWarning(destinations.get(destination.value)));
      results.hidden = false;
      status.textContent = `Đã tạo gợi ý cho ${data.destination.destinationName}.`;
    } catch (error) {
      results.hidden = true;
      status.textContent = error.message;
    } finally {
      button.disabled = false;
    }
  };

  results.addEventListener("click", event => {
    const suggestion = event.target.closest(".recommendation-suggestion");
    if (!suggestion) return;
    const container = suggestion.dataset.recommendKind === "place" ? placeList : serviceList;
    let input = Array.from(container.querySelectorAll("input"))
      .find(item => item.value === suggestion.dataset.recommendValue);
    if (!input && suggestion.dataset.recommendKind === "place") {
      const label = document.createElement("label");
      const text = document.createElement("span");
      input = document.createElement("input");
      label.className = "recommendation-place";
      input.type = "checkbox";
      input.value = suggestion.dataset.recommendValue;
      text.textContent = suggestion.dataset.recommendValue;
      label.append(input, text);
      container.append(label);
    }
    if (!input || input.checked) return;
    input.checked = true;
    submitRecommendations();
  });

  button.addEventListener("click", submitRecommendations);

  loadDestinations();
});
