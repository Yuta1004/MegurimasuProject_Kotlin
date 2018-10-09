# 先読みソルバークラス(ReadAheadSolver)
何手か先まで読んで最適な手を探す
strategyOf...から始まる関数は作戦関数(作戦に基づいて手を選ぶ)

## 関数
- main(args: Array<String>)
	- メイン関数
	- これが一番最初に呼ばれる

- strategyOfBruteForce(agentName: String): List<Int>
	- ゴリ押し戦法

- strategyOfStalker(agentName: String): List<int>
	- ストーカー戦法

- strategyOfPrayToGod(agentName: String): List<Int>
	- ランダム

- getAgentPosFromQR(): Map<String, Array<Int>>
	- QRデータからエージェントの初期位置を読み込んで返す

- getScoreDataFromQR(): Array<Array<Int>>
	- QRデータからスコアデータを読み込んで返す
