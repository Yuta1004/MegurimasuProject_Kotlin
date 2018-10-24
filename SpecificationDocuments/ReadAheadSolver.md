# 先読みソルバー(ReadAheadSolver)
何手か先まで読んで最適な手を探す
strategyOf...から始まる関数は作戦関数(作戦に基づいて手を選ぶ)

## 関数
- solver.fortest.fortest.main(args: Array<String>)
	- メイン関数
	- これが一番最初に呼ばれる

- solver.searchBestBehavior(megurimasu: MegurimasuSimulator, depth: Int, probability: Array<Int>): Pair<Int, Map<String, Int>>
	- depth -> ゲーム木の深さ, probability -> ゴリ押し・ストーカー・ランダムをどれくらい採用するか
	- ゲーム木で探索を行ってもっとも良いスコアとなる手を返す

- solver.strategyOfBruteForce(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>
	- ゴリ押し戦法
	- 2手先までのスコアを計算して一番高いスコアが得られる行動タイプを選択する

- solver.strategyOfStalker(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<int>
	- ストーカー戦法
	- 自分の座標と相手の座標から角度を算出して，その角度にあった行動タイプを選択する

- solver.strategyOfPrayToGod(num: Int): List<Int>
	- ランダム
	- [0~7, 10~17]のランダムな値をnum個生成してListにして返すgit 
