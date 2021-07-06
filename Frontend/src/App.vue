<template>
  <div id="app">
    <h1>Scrabble szóajánló</h1>

    <div class="columns">
      <div class="column is-narrow">
        <tiles></tiles>
        <div class="columns">
          <div class="column is-narrow is-offset-4">
            <div id="recommended_words">
              <p v-if="showLoadingText">Betöltés...</p>
              <p v-if="recommendedWords.length" id="top_text">Top 5 kirakható szó</p>
              <p v-for="recommendedWord in recommendedWords" id="recommended_word" :key="recommendedWord.id">
                {{ (recommendedWord.id + 1) + '. ' + recommendedWord.word + ' (' + recommendedWord.score + ' pont)' }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <div class="column">
        <game-board ref="gameBoard"></game-board>
      </div>
    </div>

    <div class="columns">
      <div class="column is-narrow">
        <div id="main_buttons">
          <table>
            <tr>
              <td>
                <b-button class="is-dark" size="is-large" @click="resetGame">Újrakezdés</b-button>
              </td>
              <td>
                <b-button class="is-dark" size="is-large" @click="recommendWords">Szóajánlás</b-button>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <div class="column is-offset-1">
        <player-tiles ref="playerTiles"></player-tiles>
      </div>
    </div>
  </div>
</template>

<script>
    import Vue from 'vue'
    import Buefy from 'buefy'
    import 'buefy/dist/buefy.css'
    import PlayerTiles from "@/components/PlayerTiles"
    import GameBoard from "@/components/GameBoard"
    import Tiles from "@/components/Tiles"
    import Vuex from 'vuex'
    import 'es6-promise/auto'
    import axios from "axios";

    Vue.use(Buefy)
    Vue.use(Vuex)

    const store = new Vuex.Store({
      state: {
        tiles : [
          {"letter": "a", "score": 1, "originalQuantity": 6, "currentQuantity": 6},
          {"letter": "e", "score": 1, "originalQuantity": 6, "currentQuantity": 6},
          {"letter": "k", "score": 1, "originalQuantity": 6, "currentQuantity": 6},
          {"letter": "t", "score": 1, "originalQuantity": 5, "currentQuantity": 5},
          {"letter": "á", "score": 1, "originalQuantity": 4, "currentQuantity": 4},
          {"letter": "l", "score": 1, "originalQuantity": 4, "currentQuantity": 4},
          {"letter": "n", "score": 1, "originalQuantity": 4, "currentQuantity": 4},
          {"letter": "r", "score": 1, "originalQuantity": 4, "currentQuantity": 4},
          {"letter": "i", "score": 1, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "m", "score": 1, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "o", "score": 1, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "s", "score": 1, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "b", "score": 2, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "d", "score": 2, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "g", "score": 2, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "ó", "score": 2, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "é", "score": 3, "originalQuantity": 3, "currentQuantity": 3},
          {"letter": "h", "score": 3, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "sz", "score": 3, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "v", "score": 3, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "f", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "gy", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "j", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "ö", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "p", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "u", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "ü", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "z", "score": 4, "originalQuantity": 2, "currentQuantity": 2},
          {"letter": "c", "score": 5, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "í", "score": 5, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ny", "score": 5, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "cs", "score": 7, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ő", "score": 7, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ú", "score": 7, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ű", "score": 7, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ly", "score": 8, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "zs", "score": 8, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "ty", "score": 10, "originalQuantity": 1, "currentQuantity": 1},
          {"letter": "joker", "score": 0, "originalQuantity": 2, "currentQuantity": 2}
        ]
      },
      getters: {
        getTiles: state => {
          return state.tiles
        }
      },
      mutations: {
         decreaseTileQuantity(state, letter) {
          let index = state.tiles.findIndex(element => element.letter === letter)
          state.tiles.splice(index, 1, {"letter":  state.tiles[index].letter, "score":  state.tiles[index].score, "originalQuantity": state.tiles[index].originalQuantity, "currentQuantity": --state.tiles[index].currentQuantity})
        },
         increaseTileQuantity(state, letter) {
          let index = state.tiles.findIndex(element => element.letter === letter)
          state.tiles.splice(index, 1, {"letter":  state.tiles[index].letter, "score":  state.tiles[index].score, "originalQuantity": state.tiles[index].originalQuantity, "currentQuantity": ++state.tiles[index].currentQuantity})
        }
      },
      actions: {
         decreaseTileQuantity(context, letter) {
          context.commit('decreaseTileQuantity', letter)
        },
         increaseTileQuantity(context, letter) {
          context.commit('increaseTileQuantity', letter)
        }
      }
    })

    export default {
      name: 'App',
      store : store,
      components: {
        Tiles,
        GameBoard,
        PlayerTiles
      },
      data() {
        return {
          recommendedWords : [],
          showLoadingText: false
        }
      },
      methods: {
        async recommendWords() {
          try {
            this.showLoadingText = true
            this.resetRecommendedWords()
            let response = await axios.get('/game/recommendation')
            let i
            for (i = 0; i < response.data.length; ++i) {
              this.recommendedWords.splice(i, 1, {"id": i, "word": response.data[i].word, "score": response.data[i].score})
            }
          } catch (error) {
            alert("A szóajánláshoz szükséges legalább egy betű a táblán, és egy betű a játékos zsetonok között.")
          }

          this.showLoadingText = false
        },
        resetRecommendedWords() {
          this.recommendedWords.splice(0)
        },
        async resetGame() {
          try {
            await axios.post('/game/reset')
            this.resetRecommendedWords()
            this.$refs.playerTiles.resetPlayerTiles()
            this.$refs.gameBoard.resetGameBoard()
          } catch (error) {
            alert('Nem engedélyezett művelet!')
          }
        }
      }
    }
</script>

<style>
h1 {
  font-size: 60px;
  font-weight: bold;
  color: brown;
  border-bottom: 1px solid;
}

#recommended_words {
  background-color: #89ed6d;
  margin-top: 10%;
}

#top_text {
  font-weight: bold;
  font-size: 16px;
  border-bottom: 1px solid;
}

p {
  font-weight: bold;
  font-size: 14px;
  color: black;
}

#recommended_word {
  font-size: 14px;
  color: black;
}
</style>