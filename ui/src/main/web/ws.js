const RECONNECT_DELAY = 5000;

const listeners = [];

let publisher = (msg) =>
  console.log("SOCKET NOT READY, FAILED TO PUBLISH", msg);

const connectSocket = () => {
  // Web socket URL, resolved from current location
  var protocol = window.location.protocol === "https:" ? "wss" : "ws";
  var hostname = window.location.host;
  var url = `${protocol}://${hostname}/ws`;

  try {
    var sock = new WebSocket(url);

    sock.onopen = () => {
      console.log("websocket opened");
      publisher = (msg) => sock.send(msg);
    };

    let handleClosed = () => {
      console.log("websocket closed, reconnecting…");
      publisher = (msg) => console.log("SOCKET CLOSED FAILED TO PUBLISH", msg);
      setTimeout(connectSocket, RECONNECT_DELAY);
    };

    sock.onclose = (e) => {
      if (e.code === 1000) {
        console.log("websocket closed gracefully");
        return;
      }
      handleClosed();
    };

    sock.onerror = (event) => {
      console.error("websocket error:", event);
      sock.close();
    };

    sock.onmessage = (msg) => {
      listeners.forEach((listener) => listener(msg));
    };
  } catch (err) {
    console.error(err);
  }
};

connectSocket();

export const addListener = (listener) => listeners.push(listener);
export const publishMessage = (msg) => publisher(msg);
