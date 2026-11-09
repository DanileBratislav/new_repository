# WorldEditMod

Небольшой Forge-мод для Minecraft 1.21.4 в стиле WorldEdit.

Что есть:
- Selection Wand (wand)
- Две точки региона: pos1 / pos2
- Заполнение региона: /we set <block>
- Замена блоков: /we replace <from> <to>
- Отмена последнего действия: /we undo

Как собрать:
1. Откройте терминал в корне проекта.
2. Выполните:
   ./gradlew build

Как использовать:
- /we wand
- /we pos1
- /we pos2
- /we set stone
- /we replace dirt grass
- /we undo

Также можно использовать Wand-элемент в руках:
- Shift + ПКМ — установить Pos1
- ПКМ — установить Pos2

Для полной функциональности WorldEdit (полигоны, копирование, паста, схемы и т. п.) этот проект можно расширять дальше.
