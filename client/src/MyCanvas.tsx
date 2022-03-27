import React from "react";

class Star {
    id: number;
    x: number;
    y: number;
    r: number;
    color: string;
    ctx: CanvasRenderingContext2D;

    constructor(id: number, x: number, y: number, ctx: CanvasRenderingContext2D) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ctx = ctx;
        this.r = Math.floor(Math.random()*2)+1;
        const alpha = (Math.floor(Math.random()*10)+1)/10/2;
        this.color = "rgba(255,255,255,"+alpha+")";
    }

    draw() {
        this.ctx.fillStyle = this.color;
        this.ctx.shadowBlur = this.r * 2;
        this.ctx.beginPath();
        this.ctx.arc(this.x, this.y, this.r, 0, 2 * Math.PI, false);
        this.ctx.closePath();
        this.ctx.fill();
    }

    move() {
        this.y -= .15 + params.backgroundSpeed/100;
        if (this.y <= -10) this.y = HEIGHT + 10;
        this.draw();
    }
}

class Dot {
    id: number;
    x: number;
    y: number;
    r: number;
    maxLinks: number;
    speed: number;
    a: number;
    aReduction: number;
    color: string;
    dir: number;
    linkColor: string;
    ctx: CanvasRenderingContext2D;

    constructor(id: number, x: number, y: number, ctx: CanvasRenderingContext2D) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ctx = ctx;
        this.r = Math.floor(Math.random()*5)+1;
        this.maxLinks = 2;
        this.speed = .5;
        this.a = .5;
        this.aReduction = .005;
        this.color = "rgba(255,255,255,"+this.a+")";
        this.linkColor = "rgba(255,255,255,"+this.a/4+")";

        this.dir = Math.floor(Math.random()*140)+200;
    }

    draw() {
        this.ctx.fillStyle = this.color;
        this.ctx.shadowBlur = this.r * 2;
        this.ctx.beginPath();
        this.ctx.arc(this.x, this.y, this.r, 0, 2 * Math.PI, false);
        this.ctx.closePath();
        this.ctx.fill();
    }

    link() {
        if (this.id == 0) return;
        const previousDot1 = getPreviousDot(this.id, 1);
        const previousDot2 = getPreviousDot(this.id, 2);
        const previousDot3 = getPreviousDot(this.id, 3);
        if (!previousDot1) return;
        this.ctx.strokeStyle = this.linkColor;
        this.ctx.moveTo(previousDot1.x, previousDot1.y);
        this.ctx.beginPath();
        this.ctx.lineTo(this.x, this.y);
        if (previousDot2 !== undefined) this.ctx.lineTo(previousDot2.x, previousDot2.y);
        if (previousDot3 !== undefined) this.ctx.lineTo(previousDot3.x, previousDot3.y);
        this.ctx.stroke();
        this.ctx.closePath();
    }

    move() {
        this.a -= this.aReduction;
        if (this.a <= 0) {
            this.die();
            return
        }
        this.color = "rgba(255,255,255,"+this.a+")";
        this.linkColor = "rgba(255,255,255,"+this.a/4+")";
        this.x = this.x + Math.cos(degToRad(this.dir))*(this.speed+params.dotsSpeed/100);
        this.y = this.y + Math.sin(degToRad(this.dir))*(this.speed+params.dotsSpeed/100);

        this.draw();
        this.link();
    }

    die() {
        delete dots[this.id];
    }
}

export function getPreviousDot(id: number, stepBack: number) {
    if (id == 0 || id - stepBack < 0) return undefined;
    if (typeof dots[id - stepBack] != "undefined") return dots[id - stepBack];
    return undefined;
}

const canvas = document.getElementById('canvas') as HTMLCanvasElement;
const context: CanvasRenderingContext2D | null = canvas.getContext('2d');
const WIDTH: number = document.documentElement.clientWidth;
const HEIGHT: number = document.documentElement.clientHeight;
let mouseMoving: boolean;
let mouseMoveChecker: any;
let mouseX: number;
let mouseY: number;
const stars: Star[] = [];
const initStarsPopulation = 80;
const dots: Dot[] = [];
const dotsMinDist = 2;
const params = {
    maxDistFromCursor: 50,
    dotsSpeed: 0,
    backgroundSpeed: 0
};

setCanvasSize();
init();

export function setCanvasSize() {
    canvas.setAttribute("width", String(WIDTH));
    canvas.setAttribute("height", String(HEIGHT));
}

export function init() {
    if (context == null) {
        return;
    }
    context.strokeStyle = "white";
    context.shadowColor = "white";
    for (let i = 0; i < initStarsPopulation; i++) {
        const xStar: number = Math.floor(Math.random() * WIDTH);
        const yStar: number = Math.floor(Math.random() * HEIGHT);
        stars[i] = new Star(i, xStar, yStar, context);
    }
    context.shadowBlur = 0;
    animate();
}

export function animate() {
    if (context == null) {
        return;
    }
    context.clearRect(0, 0, WIDTH, HEIGHT);

    for (const i in stars) {
        stars[i].move();
    }
    for (const i in dots) {
        dots[i].move();
    }
    drawIfMouseMoving();
    requestAnimationFrame(animate);
}

window.onmousemove = function (e) {
    mouseMoving = true;
    mouseX = e.clientX;
    mouseY = e.clientY;
    clearInterval(mouseMoveChecker);
    mouseMoveChecker = setTimeout(function () {
        mouseMoving = false;
    }, 100);
}


export function drawIfMouseMoving() {
    if (!mouseMoving || context == null) return;

    if (dots.length == 0) {
        dots[0] = new Dot(0, mouseX, mouseY, context);
        dots[0].draw();
        return;
    }

    const previousDot = getPreviousDot(dots.length, 1);
    if (previousDot == undefined) return;
    const prevX = previousDot.x;
    const prevY = previousDot.y;

    const diffX = Math.abs(prevX - mouseX);
    const diffY = Math.abs(prevY - mouseY);

    if (diffX < dotsMinDist || diffY < dotsMinDist) return;

    let xVariation = Math.random() > .5 ? -1 : 1;
    xVariation = xVariation * Math.floor(Math.random() * params.maxDistFromCursor) + 1;
    let yVariation = Math.random() > .5 ? -1 : 1;
    yVariation = yVariation * Math.floor(Math.random() * params.maxDistFromCursor) + 1;
    dots[dots.length] = new Dot(dots.length, mouseX + xVariation, mouseY + yVariation, context);
    dots[dots.length - 1].draw();
    dots[dots.length - 1].link();
}

export function degToRad(deg: number) {
    return deg * (Math.PI / 180);
}

function MyCanvas() {
    return (
        <div className="my-canvas">
            <div className="landscape"/>
            <div className="filter"/>
        </div>
    )
}

export default MyCanvas;
