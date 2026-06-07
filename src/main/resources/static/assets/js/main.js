// =========================
// NEWVILLA FULL UI SCRIPT
// =========================

document.addEventListener("DOMContentLoaded", () => {
  const siteHeader = document.getElementById("siteHeader");
  const backToTop = document.getElementById("backToTop");
  const scrollProgress = document.getElementById("scrollProgress");
  const tabButtons = document.querySelectorAll(".tab-btn");
  const pageTransition = document.getElementById("pageTransition");

  // Header sticky, scroll progress, back to top
  const handleScroll = () => {
    const scrollTop = window.scrollY;
    const height = document.documentElement.scrollHeight - window.innerHeight;
    const percent = height > 0 ? (scrollTop / height) * 100 : 0;

    if (siteHeader) {
      siteHeader.classList.toggle("is-sticky", scrollTop > 80);
    }

    if (backToTop) {
      backToTop.classList.toggle("show", scrollTop > 240);
    }

    if (scrollProgress) {
      scrollProgress.style.width = `${percent}%`;
    }
  };

  window.addEventListener("scroll", handleScroll);
  handleScroll();

  if (backToTop) {
    backToTop.addEventListener("click", () => {
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
  }

  // Reveal animation on scroll
  const revealItems = document.querySelectorAll(".reveal");
  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add("revealed");
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.13 });

  revealItems.forEach(item => revealObserver.observe(item));

  // Counter animation
  const counters = document.querySelectorAll(".counter");
  const counterObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (!entry.isIntersecting) return;

      const counter = entry.target;
      const target = Number(counter.dataset.target || 0);
      const duration = 1100;
      const startTime = performance.now();

      const update = (time) => {
        const progress = Math.min((time - startTime) / duration, 1);
        counter.textContent = Math.floor(progress * target).toString();

        if (progress < 1) {
          requestAnimationFrame(update);
        } else {
          counter.textContent = target.toString();
        }
      };

      requestAnimationFrame(update);
      counterObserver.unobserve(counter);
    });
  }, { threshold: 0.6 });

  counters.forEach(counter => counterObserver.observe(counter));

  // Search tabs
  tabButtons.forEach(button => {
    button.addEventListener("click", () => {
      tabButtons.forEach(btn => btn.classList.remove("active"));
      button.classList.add("active");

      const statusInput = document.getElementById("statusInput");
      if (statusInput) {
        statusInput.value = button.dataset.type === "rent" ? "Theo yêu cầu" : "Đang mở bán";
      }
    });
  });

  // Favorite icon effect
  document.addEventListener("click", event => {
    const favButton = event.target.closest(".fav-btn");
    if (!favButton) return;

    event.preventDefault();
    event.stopPropagation();

    const icon = favButton.querySelector("i");
    icon.classList.toggle("fa-regular");
    icon.classList.toggle("fa-solid");
    favButton.classList.toggle("active");
  });

  // Detail gallery
  const mainGalleryImage = document.getElementById("mainGalleryImage");
  const galleryThumbs = document.querySelectorAll(".gallery-thumbs button");

  galleryThumbs.forEach(button => {
    button.addEventListener("click", () => {
      const src = button.dataset.src;

      if (!src || !mainGalleryImage) return;

      galleryThumbs.forEach(btn => btn.classList.remove("active"));
      button.classList.add("active");

      mainGalleryImage.style.opacity = "0";
      setTimeout(() => {
        mainGalleryImage.src = src;
        mainGalleryImage.style.opacity = "1";
      }, 160);
    });
  });

  // Bật/tắt hiển thị mật khẩu
  document.querySelectorAll(".password-toggle").forEach(button => {
    button.addEventListener("click", () => {
      const input = button.parentElement.querySelector("input");
      const icon = button.querySelector("i");

      if (!input) return;

      const showing = input.type === "text";
      input.type = showing ? "password" : "text";

      if (icon) {
        icon.classList.toggle("fa-eye");
        icon.classList.toggle("fa-eye-slash");
      }
    });
  });

  // Smooth page transition for internal links
  document.querySelectorAll("a[href]").forEach(link => {
    link.addEventListener("click", event => {
      const href = link.getAttribute("href");

      if (!href || href.startsWith("#") || href.startsWith("mailto:") || href.startsWith("tel:") || href.startsWith("http")) {
        return;
      }

      if (link.target === "_blank" || event.ctrlKey || event.metaKey) {
        return;
      }

      if (pageTransition) {
        event.preventDefault();
        pageTransition.classList.add("active");
        setTimeout(() => {
          window.location.href = href;
        }, 240);
      }
    });
  });

  // Magnetic button and tilt cards
  document.querySelectorAll(".magnetic-btn").forEach(button => {
    button.addEventListener("mousemove", event => {
      const rect = button.getBoundingClientRect();
      const x = event.clientX - rect.left - rect.width / 2;
      const y = event.clientY - rect.top - rect.height / 2;
      button.style.transform = `translate(${x * 0.08}px, ${y * 0.12}px)`;
    });

    button.addEventListener("mouseleave", () => {
      button.style.transform = "";
    });
  });

  document.querySelectorAll(".tilt-card").forEach(card => {
    card.addEventListener("mousemove", event => {
      const rect = card.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;
      const rotateY = ((x / rect.width) - 0.5) * 5;
      const rotateX = ((y / rect.height) - 0.5) * -5;
      card.style.transform = `perspective(900px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-3px)`;
    });

    card.addEventListener("mouseleave", () => {
      card.style.transform = "";
    });
  });
});
