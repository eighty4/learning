document.getElementById('ping').addEventListener('click', () => {
    console.log(window.ping())
    const pong = document.createElement('div')
    pong.textContent = 'Pong'
    document.body.appendChild(pong)
})
