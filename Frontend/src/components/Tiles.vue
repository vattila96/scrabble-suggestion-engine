<template>
  <div id="tiles">
    <p id="title">Zsetonok</p>
    <table>
      <tr v-for="row in tilesMatrix" :key="row.id">
        <td v-for="element in row" :key="element.id">
          <button :class="[isTileInStock(element.letter) ? '' : 'disabled_tile', 'tile']" :draggable="isTileInStock(element.letter)" @dragstart="startDrag($event, element)">
            <p>{{ element.letter + ' (' + element.currentQuantity + 'x)' }}</p>
          </button>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: "Tiles",

  computed: {
    ...mapGetters([
        'getTiles'
    ]),
    tilesMatrix: function() {
        let tilesMatrix = [[], [], [], [], []]
        let tmpArray = this.getTiles.slice()

        while (tmpArray.length) tilesMatrix.push(tmpArray.splice(0,5))

        return tilesMatrix
    }
  },
  methods: {
    startDrag: (evt, tile) => {
      evt.dataTransfer.dropEffect = 'move'
      evt.dataTransfer.effectAllowed = 'move'
      evt.dataTransfer.setData('tileLetter', tile.letter)
      evt.dataTransfer.setData('tileScore', tile.score)
    },
    isTileInStock(letter) {
      let index = this.getTiles.findIndex(element => element.letter === letter)

      return this.getTiles[index].currentQuantity > 0
    },
  }
}
</script>

<style scoped>
.tile {
  height: 100%;
  width: 100%;
  background-color: #d6aa3a;
}

.disabled_tile {
  background-color: #f56c42;
}

.tile:hover {
  background-color: #f5d442;
}

td {
  height: 30px;
  width: 82px;
}

#title {
  font-size: 20px;
  font-weight: bold;
  color: brown;
}
</style>