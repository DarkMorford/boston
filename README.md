# Boston

Tracker to dump chunk information / player position to obs/xpslit. Runs on `0.0.0.0:9292`. Built for the [Punch-A-Chunk](https://twitter.com/loadingreadyrun/status/1114713006112251905) challenge.

## API

### `GET /`

Returns the position of each player connected, the blocks they have broken and the items they have built (crafted or forged).

```js
{
  "Test": {
    "objectsBroken": {
      "minecraft:dirt": 21,
      "minecraft:grass_block": 40,
      "minecraft:wooden_shovel": 1
    },
    "itemsBuilt": {},
    "playerPosition": {
      "left": -43,
      "middle": 74,
      "right": 3
    }
  }
}
```

### `GET /?chunkX=<x>&chunkZ=<z>`

Returns the count of all solid blocks in an x,z chunk.

```js
{
  "Dirt": 873,
  "Spruce Log": 32,
  "Redstone Ore": 18,
  "Iron Ore": 113,
  "Bedrock": 790,
  "Stone": 14595,
  "Granite": 832,
  "Diamond Ore": 3,
  "Grass Block": 251,
  "Coal Ore": 241,
  "Diorite": 675,
  "Andesite": 742,
  "Oak Planks": 104,
  "Oak Fence": 12,
  "Gravel": 475,
  "_solidBlocks": 19756
}
```
