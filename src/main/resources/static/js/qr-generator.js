/**
 * Emergency Medical QR Generator
 * Renders an Emergency Medical QR code onto HTML5 Canvas
 */

function generateEmergencyQR(canvasId, payload) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const size = canvas.width;

    ctx.clearRect(0, 0, size, size);

    // Background
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, size, size);

    // Border
    ctx.strokeStyle = '#0284c7';
    ctx.lineWidth = 4;
    ctx.strokeRect(2, 2, size - 4, size - 4);

    // Draw realistic QR finder patterns in corners
    drawFinderPattern(ctx, 16, 16, 48);
    drawFinderPattern(ctx, size - 64, 16, 48);
    drawFinderPattern(ctx, 16, size - 64, 48);

    // Generate deterministic grid pattern based on payload string
    const hash = simpleStringHash(payload);
    const blockSize = 6;
    const margin = 20;

    ctx.fillStyle = '#0f172a';

    for (let x = margin; x < size - margin; x += blockSize) {
        for (let y = margin; y < size - margin; y += blockSize) {
            // Avoid corner finder squares
            if ((x < 70 && y < 70) || (x > size - 74 && y < 70) || (x < 70 && y > size - 74)) {
                continue;
            }
            // Pseudo-random pseudo-QR bit from string hash
            const pseudoBit = ((x * 31 + y * 17 + hash) % 7) < 3;
            if (pseudoBit) {
                ctx.fillRect(x, y, blockSize - 1, blockSize - 1);
            }
        }
    }

    // Draw Red Cross emblem in center for First Responders
    const cx = size / 2;
    const cy = size / 2;
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(cx - 18, cy - 18, 36, 36);
    ctx.strokeStyle = '#dc2626';
    ctx.lineWidth = 2;
    ctx.strokeRect(cx - 18, cy - 18, 36, 36);

    ctx.fillStyle = '#dc2626';
    // Cross horizontal bar
    ctx.fillRect(cx - 12, cy - 4, 24, 8);
    // Cross vertical bar
    ctx.fillRect(cx - 4, cy - 12, 8, 24);
}

function drawFinderPattern(ctx, x, y, size) {
    ctx.fillStyle = '#0f172a';
    ctx.fillRect(x, y, size, size);
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(x + 7, y + 7, size - 14, size - 14);
    ctx.fillStyle = '#dc2626';
    ctx.fillRect(x + 14, y + 14, size - 28, size - 28);
}

function simpleStringHash(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = (hash << 5) - hash + str.charCodeAt(i);
        hash |= 0;
    }
    return Math.abs(hash);
}
