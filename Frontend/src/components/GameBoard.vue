<template>
  <div id="game_board">
    <p id="title">Játék tábla</p>
    <table>
      <tr v-for="(row, i) in gameBoard" :key="row.id">
        <td v-for="(element, j) in row" :key="element.id">
         <button class="game_board_tile" @dblclick="removeTileFromGameBoard(i, j)" @drop="addTileToGameBoard($event, i, j)" @dragover.prevent @dragenter.prevent>
          <p v-if="element.letter">
            {{ element.letter + ' ' + element.score }}
          </p>
        </button>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import {mapActions} from "vuex";
import axios from "axios";

export default {
  name: "GameBoard",
  data() {
    return {
      gameBoard : [[], [], [], [], [], [], [], [], [], [], [], [], [], [], []]
    }
  },
  methods: {
    ...mapActions([
      'decreaseTileQuantity',
      'increaseTileQuantity'
    ]),
    initializeGameBoard() {
      let i, j
      for (i = 0; i < 15; ++i) {
        for (j = 0; j < 15; ++j) {
          this.gameBoard[i][j] = {id : i.toString() + j.toString(), letter: '', score: 0}
        }
      }
    },
    setGameBoardElement(i, j, newLetter, newScore) {
      this.gameBoard[i].splice(j, 1, {"id": i.toString() + j.toString(), "letter": newLetter, "score": parseInt(newScore)})
    },
    async addTileToGameBoard(evt, i, j) {
      if (!this.gameBoard[i][j].letter) {

        try {
          let tileLetter = evt.dataTransfer.getData('tileLetter')
          let tileScore = evt.dataTransfer.getData('tileScore')
          await axios.post('/game/gameboard-tiles', {"tile": {"letter": tileLetter, "score": tileScore}, "x": i, "y": j})
          this.$parent.resetRecommendedWords()
          this.setGameBoardElement(i, j, tileLetter, tileScore)
          this.decreaseTileQuantity(tileLetter)
        } catch (error) {
          alert('Nem engedélyezett művelet!')
        }
      }
    },
    async removeTileFromGameBoard(i, j) {
      if (this.gameBoard[i][j].letter) {

        try {
          let currentTile = this.gameBoard[i][j]
          await axios.delete('/game/gameboard-tiles', { data: {"tile": {"letter": currentTile.letter, "score": currentTile.score}, "x": i, "y": j}})
          this.$parent.resetRecommendedWords()
          this.increaseTileQuantity(currentTile.letter)
          this.setGameBoardElement(i, j, '', 0)
        } catch (error) {
          alert('Nem engedélyezett művelet!')
        }
      }
    },
    resetGameBoard() {
      let i, j
      for (i = 0; i < this.gameBoard.length; ++i) {
        for (j = 0; j < this.gameBoard[i].length; ++j) {
          if (this.gameBoard[i][j].letter) {
            this.increaseTileQuantity(this.gameBoard[i][j].letter)
            this.gameBoard[i].splice(j, 1, {"id": i.toString() + j.toString(), "letter": '', "score": 0})
          }
        }
      }
    }
  },
  created: function() {
    this.initializeGameBoard()
  }
}
</script>

<style scoped>
.game_board_tile {
  background-color: #d6aa3a;
  height: 100%;
  width: 100%;
}

td {
  height: 28px;
  width: 60px;
}

#title {
  font-size: 20px;
  font-weight: bold;
  color: brown;
}
</style>