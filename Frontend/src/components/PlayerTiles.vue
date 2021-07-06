<template>
  <div id="player_tiles">
    <p id="title">Játékos zsetonjai</p>
    <table>
      <tr>
        <td v-for="playerTile in playerTiles" :key="playerTile.id">
          <button class="player_tile" @dblclick="removeTileFromPlayerTiles(playerTile.id)" @drop="addTileToPLayerTiles($event, playerTile.id)" @dragover.prevent @dragenter.prevent>
            <p v-if="playerTile.letter">
              {{ playerTile.letter + ' ' + playerTile.score }}
            </p>
          </button>
        </td>
        <td>
          <b-button id="random" @click="randomizePlayerTiles" type="is-dark">Randomizálás</b-button>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import {mapActions} from "vuex";
import axios from 'axios';

export default {
  name: "PlayerTiles",
  data() {
    return {
      playerTiles: [
        {"id": 0, "letter": '', "score": 0},
        {"id": 1, "letter": '', "score": 0},
        {"id": 2, "letter": '', "score": 0},
        {"id": 3, "letter": '', "score": 0},
        {"id": 4, "letter": '', "score": 0},
        {"id": 5, "letter": '', "score": 0},
        {"id": 6, "letter": '', "score": 0}
      ]
    }
  },
  methods: {
    ...mapActions([
        'decreaseTileQuantity',
        'increaseTileQuantity'
    ]),
    setPlayerTilesElement(id, newLetter, newScore) {
      this.playerTiles.splice(id, 1, {"id": id, "letter": newLetter, "score": parseInt(newScore)})
    },
    async addTileToPLayerTiles(evt, playerTileId) {
      if (!this.playerTiles[playerTileId].letter) {

        try {
          let tileLetter = evt.dataTransfer.getData('tileLetter')
          let tileScore = evt.dataTransfer.getData('tileScore')
          await axios.post('/game/player-tiles', {"tile": {"letter": tileLetter, "score": tileScore}, "index": playerTileId})
          this.$parent.resetRecommendedWords()
          this.setPlayerTilesElement(playerTileId, tileLetter, tileScore)
          this.decreaseTileQuantity(tileLetter)
        } catch (error) {
          alert('Nem engedélyezett művelet!')
        }
      }
    },
     async removeTileFromPlayerTiles(playerTileId) {
      if (this.playerTiles[playerTileId].letter) {

        try {
          let currentTile = this.playerTiles[playerTileId]
          await axios.delete('/game/player-tiles', { data: {"tile": {"letter": currentTile.letter, "score": currentTile.score}, "index": playerTileId}})
          this.$parent.resetRecommendedWords()
          this.increaseTileQuantity(currentTile.letter)
          this.setPlayerTilesElement(playerTileId, '', 0)
        } catch (error) {
          alert('Nem engedélyezett művelet!')
        }
      }
    },
    resetPlayerTiles() {
      let i;
      for (i = 0; i < this.playerTiles.length; ++i) {
        if (this.playerTiles[i].letter) {
          this.increaseTileQuantity(this.playerTiles[i].letter)
          this.playerTiles.splice(i, 1, {"id": i, "letter": '', "score": 0})
        }
      }
    },
    async randomizePlayerTiles() {
      try {
        let response = await axios.get('/game/player-tiles/randomization')
        this.resetPlayerTiles()
        this.$parent.resetRecommendedWords()
        let i
        for (i = 0; i < response.data.length; ++i) {
          this.playerTiles.splice(i, 1, {"id": i, "letter": response.data[i].letter, "score": response.data[i].score})
          this.decreaseTileQuantity(response.data[i].letter)
        }
      } catch (error) {
        alert("A randomizáláshoz legalább 7 zseton szükséges!")
      }
    }
  }
}
</script>

<style scoped>
.player_tile {
  height: 100%;
  width: 100%;
  background-color: #d6aa3a;
}

td {
  height: 28px;
  width: 64px;
}

#random {
  width: 70%;
  height: 100%;
  margin-left: 10px;
}

#title {
  font-size: 20px;
  font-weight: bold;
  color: brown;
}
</style>