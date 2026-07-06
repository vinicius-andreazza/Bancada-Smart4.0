/**
 * bloco-3d-viewer.component.ts
 *
 * Renders the FULL pedido as a 3D stack of blocks (one per "andar"),
 * each block with 3 open sides (slots) where lâminas are inserted,
 * and a tampa on top of the highest block.
 *
 * Install: npm install three
 */
import {
  Component,
  ElementRef,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild,
  input,
} from '@angular/core';
import * as THREE from 'three';

import { CorBloco } from '../../../../core/models/enums/corbloco.enum';
import { CorLamina } from '../../../../core/models/enums/corlamina.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';
import { PadraoLamina } from '../../../../core/models/enums/padraolamina.enum';
import { PosicaoLamina } from '../../../../core/models/enums/posicaolamina.enum';

export interface LaminaConfig {
  corLamina:     CorLamina;
  padraoLamina:  PadraoLamina;
  posicaoLamina: PosicaoLamina;
}

/** One block (one "andar") of the pedido. */
export interface BlocoConfig {
  andar:    number;
  corBloco: CorBloco;
  laminas:  LaminaConfig[];
}

/** Full pedido: ordered list of blocks (andar 1 = bottom) + lid for the topmost block. */
export interface PedidoPreviewConfig {
  blocos:   BlocoConfig[];
  corTampa: CorTampa | null;
}

// ── Color maps ───────────────────────────────────────────────────────────
const BLOCO_COLOR: Record<CorBloco, number> = {
  [CorBloco.VAZIO]:    0x8b97a8,
  [CorBloco.PRETO]:    0x1c1c1c,
  [CorBloco.VERMELHO]: 0xc41e1e,
  [CorBloco.AZUL]:     0x1a4dcc,
};
const BLOCO_DARK: Record<CorBloco, number> = {
  [CorBloco.VAZIO]:    0x5e6a7a,
  [CorBloco.PRETO]:    0x0d0d0d,
  [CorBloco.VERMELHO]: 0x8b1010,
  [CorBloco.AZUL]:     0x103298,
};
const TAMPA_COLOR: Record<CorTampa, number> = {
  [CorTampa.PRETO]:    0x1c1c1c,
  [CorTampa.VERMELHO]: 0xc41e1e,
  [CorTampa.AZUL]:     0x1a4dcc,
};
const LAMINA_COLOR: Record<CorLamina, number> = {
  [CorLamina.VERMELHO]: 0xe53e3e,
  [CorLamina.AZUL]:     0x2b6cb0,
  [CorLamina.AMARELO]:  0xd69e2e,
  [CorLamina.VERDE]:    0x276749,
  [CorLamina.PRETO]:    0x171923,
  [CorLamina.BRANCO]:   0xe2e8f0,
};

// ── Canvas texture for lâmina with pattern ─────────────────────────────────
function makeLaminaTex(cor: CorLamina, padrao: PadraoLamina): THREE.CanvasTexture {
  const W = 256, H = 160;
  const c = document.createElement('canvas');
  c.width = W; c.height = H;
  const ctx = c.getContext('2d')!;

  const hex = LAMINA_COLOR[cor];
  ctx.fillStyle = `#${hex.toString(16).padStart(6, '0')}`;
  ctx.fillRect(0, 0, W, H);

  const g = ctx.createLinearGradient(0, 0, 0, H);
  g.addColorStop(0, 'rgba(255,255,255,0.15)');
  g.addColorStop(1, 'rgba(0,0,0,0.2)');
  ctx.fillStyle = g;
  ctx.fillRect(0, 0, W, H);

  if (padrao !== PadraoLamina.NENHUM) {
    ctx.strokeStyle = 'rgba(0,0,0,0.6)';
    ctx.lineWidth = 3;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    const u = Math.min(W, H) * 0.28;
    const cx = W * 0.5, cy = H * 0.5;

    if (padrao === PadraoLamina.CASA) {
      const bx = cx - u * 0.4, by = cy - u * 0.45;
      const bw = u * 0.8, bh = u * 0.7;
      ctx.beginPath(); ctx.rect(bx, by + u * 0.3, bw, bh - u * 0.3); ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(bx - u * 0.08, by + u * 0.35);
      ctx.lineTo(cx, by);
      ctx.lineTo(bx + bw + u * 0.08, by + u * 0.35);
      ctx.stroke();
      ctx.beginPath(); ctx.rect(cx - u * 0.09, by + bh * 0.5, u * 0.18, bh * 0.5); ctx.stroke();
      ctx.beginPath(); ctx.rect(bx + u * 0.1, by + bh * 0.3, u * 0.22, u * 0.22); ctx.stroke();

    } else if (padrao === PadraoLamina.NAVIO) {
      const sy = cy;
      ctx.beginPath(); ctx.rect(cx - u * 0.5, sy, u, u * 0.4); ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(cx + u * 0.5, sy + u * 0.4);
      ctx.lineTo(cx + u * 0.65, sy + u * 0.22);
      ctx.stroke();
      ctx.beginPath(); ctx.rect(cx - u * 0.22, sy - u * 0.38, u * 0.44, u * 0.4); ctx.stroke();
      for (let i = 0; i < 2; i++) {
        ctx.beginPath();
        ctx.rect(cx - u * 0.14 + i * u * 0.2, sy - u * 0.7, u * 0.09, u * 0.34);
        ctx.stroke();
      }
      for (let i = 0; i < 3; i++) {
        ctx.beginPath();
        ctx.arc(cx - u * 0.3 + i * u * 0.3, sy + u * 0.2, u * 0.07, 0, Math.PI * 2);
        ctx.stroke();
      }

    } else if (padrao === PadraoLamina.ESTRELA) {
      ctx.beginPath();
      const spikes = 5, outerR = u * 0.48, innerR = u * 0.22;
      let rot = -Math.PI / 2;
      const step = Math.PI / spikes;
      ctx.moveTo(cx + Math.cos(rot) * outerR, cy + Math.sin(rot) * outerR);
      for (let i = 0; i < spikes; i++) {
        rot += step;
        ctx.lineTo(cx + Math.cos(rot) * innerR, cy + Math.sin(rot) * innerR);
        rot += step;
        ctx.lineTo(cx + Math.cos(rot) * outerR, cy + Math.sin(rot) * outerR);
      }
      ctx.closePath();
      ctx.stroke();
    }
  }

  return new THREE.CanvasTexture(c);
}

// ── Minimal orbit controller (mouse / touch / wheel) ────────────────────────
class SimpleOrbit {
  spherical = new THREE.Spherical(7, Math.PI / 3.4, Math.PI / 4);
  target = new THREE.Vector3(0, 1, 0);
  private dragging = false;
  private last = { x: 0, y: 0 };
  private autoRotate = true;
  private readonly autoRotateSpeed = 0.0035;

  constructor(private camera: THREE.Camera, private domEl: HTMLElement) {
    domEl.addEventListener('mousedown',  e => this.onDown(e.clientX, e.clientY));
    domEl.addEventListener('mousemove',  e => this.onMove(e.clientX, e.clientY));
    domEl.addEventListener('mouseup',    () => this.onUp());
    domEl.addEventListener('mouseleave', () => this.onUp());
    domEl.addEventListener('touchstart', e => { e.preventDefault(); this.onDown(e.touches[0].clientX, e.touches[0].clientY); }, { passive: false });
    domEl.addEventListener('touchmove',  e => { e.preventDefault(); this.onMove(e.touches[0].clientX, e.touches[0].clientY); }, { passive: false });
    domEl.addEventListener('touchend',   () => this.onUp());
    domEl.addEventListener('wheel', e => {
      e.preventDefault();
      this.spherical.radius = Math.max(3, Math.min(16, this.spherical.radius + e.deltaY * 0.006));
    }, { passive: false });
  }

  private onDown(x: number, y: number): void {
    this.dragging = true;
    this.autoRotate = false;
    this.last = { x, y };
  }

  private onMove(x: number, y: number): void {
    if (!this.dragging) return;
    const dx = x - this.last.x;
    const dy = y - this.last.y;
    this.spherical.theta -= dx * 0.008;
    this.spherical.phi = Math.max(0.2, Math.min(Math.PI * 0.8, this.spherical.phi - dy * 0.008));
    this.last = { x, y };
  }

  private onUp(): void {
    this.dragging = false;
  }

  setTargetHeight(y: number): void {
    this.target.y = y;
  }

  update(): void {
    if (this.autoRotate) this.spherical.theta += this.autoRotateSpeed;
    const pos = new THREE.Vector3().setFromSpherical(this.spherical).add(this.target);
    this.camera.position.copy(pos);
    (this.camera as THREE.PerspectiveCamera).lookAt(this.target);
  }

  reset(radius: number, targetY: number): void {
    this.spherical.set(radius, Math.PI / 3.4, Math.PI / 4);
    this.target.set(0, targetY, 0);
    this.autoRotate = true;
  }
}

// ── Geometry constants for a single block unit ──────────────────────────────
const BS      = 1.8;   // block footprint (x / z)
const BASE_T  = 0.08;  // bottom plate thickness
const WALL_T  = 0.12;  // wall / rail thickness
const WALL_H  = 0.95;  // total open-side height
const RAIL_H  = 0.12;  // height of top/bottom rail (slot guide)
const PIN_R   = 0.05;
const PIN_HT  = 0.16;
const UNIT_H  = BASE_T + WALL_H + PIN_HT; // vertical space one block occupies in the stack

@Component({
  selector: 'app-bloco-3d-viewer',
  template: `
    <div class="flex flex-col gap-2 w-full">

      <div class="flex items-center gap-2 px-3 py-1.5 bg-gray-900/80 border-b border-gray-800 rounded-t-lg">
        <span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-pulse shrink-0"></span>
        <span class="font-mono text-[0.6rem] tracking-[0.15em] text-gray-500 uppercase">
          Pedido — Visualização 3D
        </span>
        <span class="ml-auto font-mono text-[0.55rem] text-gray-600 hidden sm:block">
          Arraste · Scroll p/ zoom
        </span>
      </div>

      <div
        #container
        class="relative w-full h-[300px] sm:h-[340px] lg:h-[380px] rounded-b-lg overflow-hidden bg-[#080d18] cursor-grab"
      >
        <canvas #canvas class="block w-full h-full"></canvas>

        <div class="pointer-events-none absolute inset-0">
          <div class="absolute top-2 left-2 w-4 h-4 border-t border-l border-sky-500/30"></div>
          <div class="absolute top-2 right-2 w-4 h-4 border-t border-r border-sky-500/30"></div>
          <div class="absolute bottom-8 left-2 w-4 h-4 border-b border-l border-sky-500/30"></div>
          <div class="absolute bottom-8 right-2 w-4 h-4 border-b border-r border-sky-500/30"></div>
        </div>

        <button
          type="button"
          (click)="resetCamera()"
          title="Resetar câmera"
          aria-label="Resetar câmera"
          class="absolute bottom-2 right-2 w-6 h-6 flex items-center justify-center rounded
                 bg-gray-800/90 border border-gray-700 text-gray-500
                 hover:text-sky-400 hover:border-sky-500/40 transition-all duration-150"
        >
          <i class="fa-solid fa-rotate text-[0.6rem]" aria-hidden="true"></i>
        </button>

        <div class="pointer-events-none absolute bottom-2 left-3 font-mono text-[0.5rem] text-gray-700 tracking-widest">
          AUTO-ROTATE
        </div>
      </div>

    </div>
  `,
})
export class Bloco3dViewerComponent implements OnInit, OnChanges, OnDestroy {
  /** Full pedido: blocks ordered bottom (andar 1) to top, plus lid color for the top block. */
  readonly pedido = input.required<PedidoPreviewConfig>();

  @ViewChild('canvas',    { static: true }) private canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('container', { static: true }) private containerRef!: ElementRef<HTMLDivElement>;

  private renderer!: THREE.WebGLRenderer;
  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private orbit!: SimpleOrbit;
  private stackGroup!: THREE.Group;
  private raf = 0;
  private ro?: ResizeObserver;

  ngOnInit(): void {
    // Sem WebGL (navegador antigo, jsdom nos testes) o preview fica vazio em
    // vez de derrubar a página inteira.
    try {
      this.initScene();
    } catch {
      return;
    }
    this.buildStack();
    this.loop();

    // ResizeObserver fires once immediately with the current size, and again
    // whenever the container size changes (including the 0 → real-size
    // transition that can happen before the grid layout settles).
    this.ro = new ResizeObserver(() => this.onResize());
    this.ro.observe(this.containerRef.nativeElement);

    // Extra safety net: re-measure on the next animation frame in case the
    // very first ResizeObserver callback already reported a 0x0 box.
    requestAnimationFrame(() => this.onResize());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.scene) return;
    if (changes['pedido']) this.buildStack();
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.raf);
    this.ro?.disconnect();
    this.renderer?.dispose();
  }

  // ── Scene setup ────────────────────────────────────────────────────────
  private initScene(): void {
    const cvs  = this.canvasRef.nativeElement;
    const cont = this.containerRef.nativeElement;
    const w = cont.clientWidth || 300;
    const h = cont.clientHeight || 300;

    this.renderer = new THREE.WebGLRenderer({ canvas: cvs, antialias: true });
    this.renderer.setSize(w, h, false);
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    // Shadows disabled — consistent flat illumination from all directions.
    this.renderer.shadowMap.enabled = false;
    this.renderer.toneMapping = THREE.NoToneMapping;

    this.scene = new THREE.Scene();
    this.scene.background = new THREE.Color(0x080d18);
    // No fog — every block must be fully visible regardless of stack height.

    this.camera = new THREE.PerspectiveCamera(44, w / h, 0.1, 80);

    // HemisphereLight provides even sky/ground illumination with no
    // directionality so surfaces never go fully dark as the model rotates.
    const hemi = new THREE.HemisphereLight(0x8899bb, 0x445566, 2.2);
    this.scene.add(hemi);

    // Four DirecionalLights spread evenly around the model so every face
    // receives the same total irradiance from any viewing angle.
    const dirs: [number, number, number][] = [
      [ 1,  1,  1],
      [-1,  1, -1],
      [ 1,  1, -1],
      [-1,  1,  1],
    ];
    for (const [x, y, z] of dirs) {
      const d = new THREE.DirectionalLight(0xffffff, 0.55);
      d.position.set(x * 6, y * 8, z * 6);
      this.scene.add(d);
    }

    // Subtle grid for spatial reference only — no shadow receiver plane needed.
    const grid = new THREE.GridHelper(14, 28, 0x111a30, 0x0a1020);
    grid.position.y = -0.015;
    this.scene.add(grid);

    this.orbit = new SimpleOrbit(this.camera, cvs);
  }

  // ── Build / rebuild the stack ─────────────────────────────────────────
  private clearStack(): void {
    if (!this.stackGroup) return;
    this.scene.remove(this.stackGroup);
    this.stackGroup.traverse(obj => {
      if (obj instanceof THREE.Mesh) {
        obj.geometry.dispose();
        const mats = Array.isArray(obj.material) ? obj.material : [obj.material];
        mats.forEach(m => {
          const tex = (m as THREE.MeshStandardMaterial).map;
          tex?.dispose();
          m.dispose();
        });
      }
    });
  }

  private buildStack(): void {
    this.clearStack();
    this.stackGroup = new THREE.Group();
    this.scene.add(this.stackGroup);

    const { blocos, corTampa } = this.pedido();
    const total = Math.max(blocos.length, 1);

    blocos.forEach((bloco, i) => {
      const yOffset = i * UNIT_H;
      const isTop = i === blocos.length - 1;
      this.buildBlock(bloco, yOffset, isTop ? corTampa : null);
    });

    // Frame the camera around the whole stack
    const stackHeight = total * UNIT_H;
    this.orbit.setTargetHeight(stackHeight / 2);
    const dist = Math.max(5, stackHeight * 1.6 + 2.5);
    if (this.orbit.spherical.radius < 3) this.orbit.spherical.radius = dist;
    this.orbit.reset(dist, stackHeight / 2);
  }

  /** Builds one block: base, back wall, 3 open-side rails (+ lâminas), corner/inner pins, optional tampa. */
  private buildBlock(bloco: BlocoConfig, yOffset: number, corTampa: CorTampa | null): void {
    const isVazio = bloco.corBloco === CorBloco.VAZIO;
    const mainHex = BLOCO_COLOR[bloco.corBloco];
    const darkHex = BLOCO_DARK[bloco.corBloco];

    const mat = (hex: number, extra: Partial<THREE.MeshStandardMaterialParameters> = {}) =>
      new THREE.MeshStandardMaterial({
        color: hex,
        roughness: 0.55,
        metalness: 0.06,
        transparent: isVazio,
        opacity: isVazio ? 0.55 : 1,
        ...extra,
      });

    const addBox = (
      w: number, h: number, d: number,
      x: number, y: number, z: number,
      m: THREE.Material,
    ): THREE.Mesh => {
      const mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), m);
      mesh.position.set(x, yOffset + y, z);
      this.stackGroup.add(mesh);
      return mesh;
    };

    // ── Bottom plate ──
    addBox(BS, BASE_T, BS, 0, BASE_T / 2, 0, mat(mainHex));

    // ── Back wall (solid, -z side — no lâmina slot here) ──
    const wallOffset = BS / 2 - WALL_T / 2;
    addBox(BS, WALL_H, WALL_T, 0, BASE_T + WALL_H / 2, -wallOffset, mat(mainHex));

    // ── Open sides: left (-x), front (+z), right (+x) — each gets a slot rail pair ──
    const openSides: PosicaoLamina[] = [
      PosicaoLamina.ESQUERDA,
      PosicaoLamina.FRENTE,
      PosicaoLamina.DIREITA,
    ];

    for (const side of openSides) {
      this.buildSlot(side, mat(darkHex, { roughness: 0.7 }), yOffset);

      const lamina = bloco.laminas.find(l => l.posicaoLamina === side);
      if (lamina) {
        this.addLamina(lamina, yOffset);
      }
    }

    // ── Corner pins (4) + 2 inner pins along the back wall ──
    const pinY = BASE_T + WALL_H + PIN_HT / 2;
    const co = BS / 2 - 0.14;
    const pinPositions: [number, number][] = [
      [-co, -co], [co, -co], [-co, co], [co, co],
      [-co * 0.45, -wallOffset + WALL_T * 0.6],
      [ co * 0.45, -wallOffset + WALL_T * 0.6],
    ];
    for (const [px, pz] of pinPositions) {
      const pin = new THREE.Mesh(
        new THREE.CylinderGeometry(PIN_R, PIN_R * 1.1, PIN_HT, 10),
        mat(darkHex, { roughness: 0.7 }),
      );
      pin.position.set(px, yOffset + pinY, pz);
      this.stackGroup.add(pin);

      const cap = new THREE.Mesh(
        new THREE.SphereGeometry(PIN_R, 10, 6),
        mat(mainHex, { roughness: 0.4 }),
      );
      cap.position.set(px, yOffset + pinY + PIN_HT / 2, pz);
      this.stackGroup.add(cap);
    }

    // ── Tampa (only on the topmost block) ──
    if (corTampa !== null) {
      const tc = TAMPA_COLOR[corTampa];
      const tampaMat = mat(tc, { transparent: false, opacity: 1, roughness: 0.42 });
      const tampaY = BASE_T + WALL_H + PIN_HT + 0.02;
      addBox(BS - 0.02, 0.11, BS - 0.02, 0, tampaY + 0.055, 0, tampaMat);
      addBox(BS + 0.015, 0.02, BS + 0.015, 0, tampaY, 0,
        mat(tc, { transparent: false, opacity: 1, roughness: 0.6 }));
    }
  }

  /**
   * Builds the C-channel slot (bottom rail + top rail) on one open side of the block,
   * representing the guide where a lâmina is inserted.
   */
  private buildSlot(side: PosicaoLamina, m: THREE.Material, yOffset: number): void {
    const slotLen = BS - WALL_T * 2;
    const railY = [BASE_T + RAIL_H / 2, BASE_T + WALL_H - RAIL_H / 2];
    const edge = BS / 2 - WALL_T / 2;

    for (const ry of railY) {
      let w: number, d: number, x: number, z: number;
      if (side === PosicaoLamina.ESQUERDA) {
        w = WALL_T; d = slotLen; x = -edge; z = 0;
      } else if (side === PosicaoLamina.DIREITA) {
        w = WALL_T; d = slotLen; x = edge; z = 0;
      } else {
        w = slotLen; d = WALL_T; x = 0; z = edge;
      }
      const mesh = new THREE.Mesh(new THREE.BoxGeometry(w, RAIL_H, d), m);
      mesh.position.set(x, yOffset + ry, z);
      this.stackGroup.add(mesh);
    }
  }

  /** Inserts a lâmina panel into the slot on the given side. */
  private addLamina(lam: LaminaConfig, yOffset: number): void {
    const lW = BS - WALL_T * 2.2;
    const lH = WALL_H - RAIL_H * 2 - 0.02;
    const lT = 0.04;
    const tex = makeLaminaTex(lam.corLamina, lam.padraoLamina);

    const mat = new THREE.MeshStandardMaterial({
      color: LAMINA_COLOR[lam.corLamina],
      roughness: 0.5,
      metalness: 0,
      map: tex,
    });

    const mesh = new THREE.Mesh(new THREE.BoxGeometry(lW, lH, lT), mat);

    const lY = BASE_T + WALL_H / 2;
    const edge = BS / 2 - WALL_T / 2;

    switch (lam.posicaoLamina) {
      case PosicaoLamina.ESQUERDA:
        mesh.rotation.y = Math.PI / 2;
        mesh.position.set(-edge, yOffset + lY, 0);
        break;
      case PosicaoLamina.FRENTE:
        mesh.position.set(0, yOffset + lY, edge);
        break;
      case PosicaoLamina.DIREITA:
        mesh.rotation.y = Math.PI / 2;
        mesh.position.set(edge, yOffset + lY, 0);
        break;
    }

    this.stackGroup.add(mesh);
  }

  // ── Render loop & resize ─────────────────────────────────────────────
  private loop = (): void => {
    this.raf = requestAnimationFrame(this.loop);
    this.orbit.update();
    this.renderer.render(this.scene, this.camera);
  };

  private onResize(): void {
    const el = this.containerRef.nativeElement;
    const w = el.clientWidth;
    const h = el.clientHeight;
    if (!w || !h) return;
    this.camera.aspect = w / h;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(w, h, false);
  }

  resetCamera(): void {
    const total = Math.max(this.pedido().blocos.length, 1);
    const stackHeight = total * UNIT_H;
    const dist = Math.max(5, stackHeight * 1.6 + 2.5);
    this.orbit.reset(dist, stackHeight / 2);
  }
}