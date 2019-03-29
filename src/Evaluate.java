public class Evaluate {
	// 棋型权重
	private static final int FIVE = 50000;// 连五
	private static final int HUO_FOUR = 5000;// 活四
	private static final int CHONG_FOUR = 1000;// 冲四
	private static final int HUO_THREE = 500;// 活三
	private static final int MIAN_THREE = 100;// 眠三
	private static final int HUO_TWO = 50;// 活二

	// 位置权重
	private int[][] blackValue;// 保存每一空位下黑子的价值
	private int[][] whiteValue;// 保存每一空位下白子的价值
	private int[][] staticValue;// 保存每一点的位置价值

	private ChessBoard cb;

	public Evaluate(ChessBoard cb) {
		this.cb = cb;
		blackValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		whiteValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		staticValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		for (int i = 0; i <= ChessBoard.COLS; i++) {
			for (int j = 0; j <= ChessBoard.ROWS; j++) {
				blackValue[i][j] = 0;
				whiteValue[i][j] = 0;
			}
		}
		for (int i = 0; i <= ChessBoard.COLS / 2; i++) {
			for (int j = 0; j <= ChessBoard.ROWS / 2; j++) {
				staticValue[i][j] = i < j ? i : j;
				staticValue[ChessBoard.COLS - i][j] = staticValue[i][j];
				staticValue[i][ChessBoard.ROWS - j] = staticValue[i][j];
				staticValue[ChessBoard.COLS - i][ChessBoard.ROWS - j] = staticValue[i][j];
			}
		}
	}

	// 空位的价值=黑棋的价值+白棋的价值+位置价值
	int[] getTheBestPosition() {// 查找计算机下棋的最佳位置
		for (int i = 0; i <= cb.COLS; i++) {
			for (int j = 0; j <= cb.ROWS; j++) {
				blackValue[i][j] = 0;
				whiteValue[i][j] = 0;
				if (cb.boardStatus[i][j] == 0) {
					for (int m = 1; m <= 4; m++) {// 每个点的分值为四个方向分值之和
						blackValue[i][j] += evaluateValue(1, i, j, m);
						whiteValue[i][j] += evaluateValue(2, i, j, m);
					}
				}
			}
		}
		int k = 0;
		int[][] totalValue = new int[(cb.COLS + 1) * (cb.ROWS + 1)][3];// 定义具有三个列的二维数组
		for (int i = 0; i <= cb.COLS; i++) {
			for (int j = 0; j <= cb.ROWS; j++) {
				if (cb.boardStatus[i][j] == 0) {
					totalValue[k][0] = i;// 第一列是该点的列坐标
					totalValue[k][1] = j;// 第二列是该店的行坐标
					totalValue[k][2] = blackValue[i][j] + whiteValue[i][j] + staticValue[i][j];// 第三列是该点的总分值
					k++;
				}
			}
		}
		sort(totalValue);// 对总分值降序排序
		// k的目的为了解决出现多个最大分值的问题，最大分值一定出现在最开始的几个位置上
		k = 1;
		int maxValue = totalValue[0][2];
		while (totalValue[k][2] == maxValue) {
			k++;
		}
		int r = (int) (Math.random() * k);// 多个点同时具有最大得分，随机选取一个作为最佳点
		int[] position = new int[2];
		position[0] = totalValue[r][0];
		position[1] = totalValue[r][1];
		return position;
	}

	private int evaluateValue(int color, int col, int row, int dir) {// 棋子放入指定位置，在指定方向上得到7个变量情况
		int k, m;
		int value = 0;
		// 空数4 连数3 空数3 下棋位置:连棋数1 空数1 连棋数2 空数2
		int chessCount1 = 1;// 放入棋子后可以形成的连续棋子数
		int chessCount2 = 0;
		int chessCount3 = 0;
		int spaceCount1 = 0;
		int spaceCount2 = 0;
		int spaceCount3 = 0;
		int spaceCount4 = 0;
		switch (dir) {
		case 1:// 水平方向
			for (k = col + 1; k <= cb.COLS; k++) {// 向增加的方向查找相同颜色连续的棋子
				if (cb.boardStatus[k][row] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k <= cb.COLS) && (cb.boardStatus[k][row] == 0)) {
				spaceCount1++;
				k++;
			}
			if (spaceCount1 == 1) {
				while ((k <= cb.COLS) && (cb.boardStatus[k][row] == color)) {
					chessCount2++;
					k++;
				}
				while ((k <= cb.COLS) && (cb.boardStatus[k][row] == 0)) {
					spaceCount2++;
					k++;
				}
			}

			for (k = col - 1; k >= 0; k--) {// 向减少的方向查找相同颜色连续的棋子
				if (cb.boardStatus[k][row] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while (k >= 0 && (cb.boardStatus[k][row] == 0)) {
				spaceCount3++;
				k--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (cb.boardStatus[k][row] == color)) {
					chessCount3++;
					k--;
				}
				while ((k >= 0) && (cb.boardStatus[k][row] == 0)) {
					spaceCount4++;
					k--;
				}
			}
			break;

		case 2:// 垂直方向
			for (k = row + 1; k <= cb.ROWS; k++) {// 向增加的方向查找相同颜色连续的棋子
				if (cb.boardStatus[col][k] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k <= cb.ROWS) && (cb.boardStatus[col][k] == 0)) {
				spaceCount1++;
				k++;
			}
			if (spaceCount1 == 1) {
				while ((k <= cb.ROWS) && (cb.boardStatus[col][k] == color)) {
					chessCount2++;
					k++;
				}
				while ((k <= cb.ROWS) && (cb.boardStatus[col][k] == 0)) {
					spaceCount2++;
					k++;
				}
			}
			for (k = row - 1; k >= 0; k--) {// 向相反的方向查找相同颜色的棋子
				if (cb.boardStatus[col][k] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k >= 0) && (cb.boardStatus[col][k] == 0)) {
				spaceCount3++;
				k--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (cb.boardStatus[col][k] == color)) {
					chessCount3++;
					k--;
				}
				while ((k >= 0) && (cb.boardStatus[col][k] == 0)) {
					spaceCount4++;
					k--;
				}
			}
			break;

		case 3:// 左上到右下
			for (k = col + 1, m = row + 1; (k <= cb.COLS) && (m <= cb.ROWS); k++, m++) {// 向增加的方向查找相同颜色的棋子
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k <= cb.COLS) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == 0)) {
				spaceCount1++;
				k++;
				m++;
			}
			if (spaceCount1 == 1) {
				while ((k <= cb.COLS) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == color)) {
					chessCount2++;
					k++;
					m++;
				}
				while ((k <= cb.COLS) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == 0)) {
					spaceCount2++;
					k++;
					m++;
				}
			}

			for (k = col - 1, m = row - 1; (k >= 0) && (m >= 0); k--, m--) {// 向相反方向查找相同颜色连续的棋子
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == 0)) {
				spaceCount3++;
				k--;
				m--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == color)) {
					chessCount3++;
					k--;
					m--;
				}
				while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == 0)) {
					spaceCount4++;
					k--;
					m--;
				}
			}
			break;

		case 4:// 右上到左下
			for (k = col + 1, m = row - 1; k <= cb.COLS && m >= 0; k++, m--) {
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while (k <= cb.COLS && m >= 0 && (cb.boardStatus[k][m] == 0)) {
				spaceCount1++;
				k++;
				m--;
			}
			if (spaceCount1 == 1) {
				while ((k <= cb.COLS) && (m >= 0) && (cb.boardStatus[k][m] == color)) {
					chessCount2++;
					k++;
					m--;
				}
				while ((k <= cb.COLS) && (m >= 0) && (cb.boardStatus[k][m] == 0)) {
					spaceCount2++;
					k++;
					m--;
				}
			}

			for (k = col - 1, m = row + 1; k >= 0 && m <= cb.ROWS; k--, m++) {// 向相反的方向查找相同颜色连续的棋子
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			while ((k >= 0) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == 0)) {
				spaceCount3++;
				k--;
				m++;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == color)) {
					chessCount3++;
					k--;
					m++;
				}
				while ((k >= 0) && (m <= cb.ROWS) && (cb.boardStatus[k][m] == 0)) {
					spaceCount4++;
					k--;
					m++;
				}
			}
			break;
		}
		if (chessCount1 + chessCount2 + chessCount3 + spaceCount1 + spaceCount2 + spaceCount3 + spaceCount4 >= 5) {
			// 只有同色棋子数+两端的空位数不少于5时，才有价值
			value = getValue(chessCount1, chessCount2, chessCount3, spaceCount1, spaceCount2, spaceCount3, spaceCount4);
		}
		return value;
	}

	private int getValue(int chessCount1, int chessCount2, int chessCount3, int spaceCount1, int spaceCount2,
			int spaceCount3, int spaceCount4) {// 根据7个变量，得到相应的棋型
		int value = 0;

		switch (chessCount1) {
		case 5:
			value = FIVE;// 连五
			break;
		case 4:
			if ((spaceCount1 > 0) && (spaceCount3 > 0)) {
				value = HUO_FOUR;// 活四 OAAAAO
			} else {
				value = CHONG_FOUR;// 冲四 OAAAA
			}
			break;
		case 3:
			if (((spaceCount1 == 1) && (chessCount2 >= 1)) && ((spaceCount3 == 1) && (chessCount3 >= 1))) {
				value = HUO_FOUR;// 活四 AOAAAOA
			} else if (((spaceCount1 == 1) && (chessCount2 >= 1)) || (((spaceCount3 == 1) && (chessCount3 >= 1)))) {
				value = CHONG_FOUR;// 冲四 AAAOA
			} else if (((spaceCount1 > 1) && (spaceCount3 > 0)) || ((spaceCount1 > 0) && (spaceCount3 > 1))) {
				value = HUO_THREE;// 活三 OOAAAO
			} else {
				value = MIAN_THREE;// 眠三
			}
			break;
		case 2:
			if ((spaceCount1 == 1) && (chessCount2 >= 2) && (spaceCount3 == 1) && (chessCount3 >= 2)) {
				value = HUO_FOUR;// 活四 AAOAAOAA
			} else if (((spaceCount1 == 1) && (chessCount2 >= 2)) || ((spaceCount3 == 1) && (chessCount3 >= 2))) {
				value = CHONG_FOUR;// 冲四 AAOAA
			} else if (((spaceCount1 == 1) && (chessCount2 == 1) && (spaceCount2 > 0) && (spaceCount3 > 0))
					|| ((spaceCount3 == 1) && (chessCount3 == 1)) && (spaceCount1 > 0) && (spaceCount4 > 0)) {
				value = HUO_THREE;// 活三 OAAOAO
			} else if ((spaceCount1 > 0) && (spaceCount4 > 0)) {
				value = HUO_TWO;
			}
			break;

		case 1:
			if (((spaceCount1 == 1) && (chessCount2 >= 3)) || (spaceCount3 == 1) && (spaceCount3 >= 3)) {
				value = CHONG_FOUR; // AOAAA
			} else if (((spaceCount1 == 1) && (chessCount2 == 2) && (spaceCount2 >= 1) && (spaceCount3 >= 1))
					|| ((spaceCount3 == 1) && (chessCount3 == 2) && (spaceCount1 >= 1) && (spaceCount4 >= 1))) {
				value = HUO_THREE;// OAOAAO
			} else if (((spaceCount1 == 1) && (chessCount2 == 2) && ((spaceCount2 >= 1) || (spaceCount3 >= 1)))
					|| ((spaceCount3 == 1) && (chessCount3 == 2) && ((spaceCount1 >= 1) || (spaceCount4 >= 1)))) {
				value = MIAN_THREE;// OAOAAO
			} else if (((spaceCount1 == 1) && (chessCount2 == 2) && (spaceCount2 > 1) && (spaceCount3 > 0))
					|| ((spaceCount3 == 1) && (chessCount3 == 1) && (spaceCount1 > 0) && (spaceCount4 > 1))) {
				value = HUO_TWO;// OAOAOO
			}
			break;
		default:
			value = 0;
			break;
		}
		return value;
	}

	private void sort(int[][] allValue) {// 使用冒泡排序法
		for (int i = 0; i < allValue.length - 1; i++) {
			for (int j = 0; j < allValue.length - 1; j++) {
				int ti, tj, tvalue;
				if (allValue[j][2] < allValue[j + 1][2]) {
					tvalue = allValue[j][2];
					allValue[j][2] = allValue[j + 1][2];
					allValue[j + 1][2] = tvalue;
					ti = allValue[j][0];
					allValue[j][0] = allValue[j + 1][0];
					allValue[j + 1][0] = ti;
					tj = allValue[j][1];
					allValue[j][1] = allValue[j + 1][1];
					allValue[j + 1][1] = tj;
				}
			}
		}
	}
}