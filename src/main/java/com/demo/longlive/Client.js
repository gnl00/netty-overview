let ws = new WebSocket("ws://localhost:5000/ws")

ws.onmessage = message => {
    console.log(message)
}

ws.send("hi")