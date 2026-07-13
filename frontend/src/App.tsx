import { useEffect, useRef, useState } from 'react'
import './App.css'

type Cell = { state: 'hidden' | 'flagged' | 'badflag' | 'revealed' | 'mine' | 'exploded'; adjacentMines: number }
type Game = { id: string; width: number; height: number; mines: number; flagsRemaining: number; elapsedSeconds: number; startedAtEpochMillis: number | null; status: 'READY' | 'PLAYING' | 'WON' | 'LOST'; board: Cell[][] }

const levels = {
  Beginner: { width: 9, height: 9, mines: 10 },
  Intermediate: { width: 16, height: 16, mines: 40 },
  Expert: { width: 30, height: 16, mines: 99 },
}

const segments: Record<string, boolean[]> = {
  '0': [true, true, true, true, true, true, false],
  '1': [false, true, true, false, false, false, false],
  '2': [true, true, false, true, true, false, true],
  '3': [true, true, true, true, false, false, true],
  '4': [false, true, true, false, false, true, true],
  '5': [true, false, true, true, false, true, true],
  '6': [true, false, true, true, true, true, true],
  '7': [true, true, true, false, false, false, false],
  '8': [true, true, true, true, true, true, true],
  '9': [true, true, true, true, false, true, true],
}

async function request(path: string, options?: RequestInit): Promise<Game> {
  const response = await fetch(`/api/games${path}`, options)
  if (!response.ok) {
    const error = await response.json().catch(() => null)
    throw new Error(error?.detail ?? error?.message ?? `Request failed (${response.status}).`)
  }
  return response.json()
}

function cellContent(cell: Cell) {
  if (cell.state === 'flagged') return <img src="/flag.png" alt="Flagged" />
  if (cell.state === 'badflag') return <img src="/badflag.png" alt="Incorrect flag" />
  if (cell.state === 'mine') return <img src="/mine.png" alt="Mine" />
  if (cell.state === 'exploded') return <img src="/mineclicked.png" alt="Exploded mine" />
  if (cell.state === 'revealed') return <img src={`/${['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight'][cell.adjacentMines]}.png`} alt={cell.adjacentMines ? `${cell.adjacentMines} adjacent mines` : 'Empty'} />
  return null
}

function adjacentFlagCount(board: Cell[][], row: number, column: number) {
  let flags = 0
  for (let neighborRow = Math.max(0, row - 1); neighborRow <= Math.min(board.length - 1, row + 1); neighborRow++) {
    for (let neighborColumn = Math.max(0, column - 1); neighborColumn <= Math.min(board[0].length - 1, column + 1); neighborColumn++) {
      if ((neighborRow !== row || neighborColumn !== column) && board[neighborRow][neighborColumn].state === 'flagged') flags++
    }
  }
  return flags
}

function DigitalCounter({ value, label }: { value: number; label: string }) {
  const digits = String(Math.min(999, Math.max(0, value))).padStart(3, '0')
  const paths = [
    'M3 1h20l-3 5H6z',
    'M20 4l4-3 2 3v17l-4 3-2-3z',
    'M20 27l4-3 2 3v17l-4 3-2-3z',
    'M3 47h20l-3-5H6z',
    'M6 27 2 24 0 27v17l4 3 2-3z',
    'M6 4 2 1 0 4v17l4 3 2-3z',
    'M3 24l3-3h14l3 3-3 3H6z',
  ]
  return <span className="digital-counter" aria-label={label}>{[...digits].map((digit, index) =>
    <svg key={index} viewBox="0 0 26 48" aria-hidden="true">{paths.map((path, segment) =>
      <path key={segment} d={path} className={segments[digit][segment] ? 'on' : ''} />
    )}</svg>
  )}</span>
}

export default function App() {
  const [game, setGame] = useState<Game>()
  const [level, setLevel] = useState<keyof typeof levels>('Expert')
  const [error, setError] = useState('')
  const [now, setNow] = useState(Date.now())
  const [timerOrigin, setTimerOrigin] = useState<number>()
  const requestVersion = useRef(0)
  const face = game?.status === 'WON' ? 'sunglasses' : game?.status === 'LOST' ? 'dead' : 'smiley'
  const elapsed = game?.status === 'PLAYING' && timerOrigin
    ? Math.min(999, Math.max(0, Math.floor((now - timerOrigin) / 1000)))
    : game?.elapsedSeconds ?? 0

  function showGame(nextGame: Game) {
    const receivedAt = Date.now()
    setGame(nextGame)
    setNow(receivedAt)
    setTimerOrigin(nextGame.status === 'PLAYING' ? receivedAt - nextGame.elapsedSeconds * 1000 : undefined)
  }

  async function newGame(nextLevel = level) {
    const version = ++requestVersion.current
    try {
      setError('')
      const nextGame = await request('', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(levels[nextLevel]) })
      if (version === requestVersion.current) showGame(nextGame)
    } catch {
      if (version === requestVersion.current) setError('Cannot reach Java backend. Start it with mvn spring-boot:run.')
    }
  }

  useEffect(() => { void newGame() }, [])
  useEffect(() => {
    if (game?.status !== 'PLAYING') return
    const timer = window.setInterval(() => setNow(Date.now()), 1000)
    return () => window.clearInterval(timer)
  }, [game?.status])

  async function play(row: number, column: number, action: 'reveal' | 'flag' | 'chord') {
    if (!game || game.status === 'WON' || game.status === 'LOST') return
    if (action === 'chord') {
      const cell = game.board[row][column]
      const flags = adjacentFlagCount(game.board, row, column)
      if (cell.state !== 'revealed' || flags !== cell.adjacentMines) return
    }
    const version = ++requestVersion.current
    try {
      setError('')
      const nextGame = await request(`/${game.id}/${action}?row=${row}&column=${column}`, { method: 'POST' })
      if (version === requestVersion.current) showGame(nextGame)
    } catch (error) {
      if (version === requestVersion.current) setError(error instanceof Error ? error.message : 'Move failed.')
    }
  }

  return <main>
    <header className="game-header">
      <label>Difficulty <select value={level} onChange={event => { const next = event.target.value as keyof typeof levels; setLevel(next); void newGame(next) }}>
        {Object.keys(levels).map(name => <option key={name}>{name}</option>)}
      </select></label>
    </header>
    {error && <p className="error">{error}</p>}
    {game && <section className="game-frame">
      <section className="controls" style={{ width: `${game.width * 2}rem` }}>
        <DigitalCounter value={game.flagsRemaining} label="Flags remaining" />
        <button className="face" onClick={() => void newGame()} aria-label="New game"><img src={`/${face}.png`} alt="" /></button>
        <DigitalCounter value={elapsed} label="Elapsed time" />
      </section>
      <section className="board" style={{ gridTemplateColumns: `repeat(${game.width}, 2rem)` }} aria-label="Minesweeper board"
        onMouseDown={event => { if (event.target === event.currentTarget) event.preventDefault() }}
        onContextMenu={event => event.preventDefault()}
        onDragStart={event => event.preventDefault()}>
        {game.board.flatMap((row, rowIndex) => row.map((cell, columnIndex) => <button
          key={`${rowIndex}-${columnIndex}`}
          className={`cell ${cell.state} ${cell.state === 'revealed' ? `n${cell.adjacentMines}` : ''}`}
          onClick={() => void play(rowIndex, columnIndex, 'reveal')}
          onMouseDown={event => {
            if (event.buttons === 3) {
              event.preventDefault()
              void play(rowIndex, columnIndex, 'chord')
            }
          }}
          onContextMenu={event => { event.preventDefault(); void play(rowIndex, columnIndex, 'flag') }}
          aria-label={`Row ${rowIndex + 1}, column ${columnIndex + 1}`}
        >{cellContent(cell)}</button>))}
      </section>
    </section>}
  </main>
}
