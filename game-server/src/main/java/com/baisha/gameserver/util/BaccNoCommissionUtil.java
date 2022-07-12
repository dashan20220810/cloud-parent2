package com.baisha.gameserver.util;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.model.bo.game.BetAwardBO;
import com.baisha.gameserver.model.bo.game.GameBaccOddsBO;
import com.baisha.modulecommon.enums.TgBaccRuleEnum;

import java.math.BigDecimal;

/**
 * 百家乐免佣玩法
 *
 * @author yihui
 */
public class BaccNoCommissionUtil {

    /**
     * 与中奖选项 对比 是否 中奖
     * Z（庄）  X（闲）  H（和） ZD（庄对） XD（闲对） D（庄对 闲对）SB（庄对 闲对 和）SS（幸运6）
     *
     * @param bet
     * @param awardOption
     * @param gameBaccOdds
     * @return
     */
    public static BetAwardBO getBetAward(Bet bet, String awardOption, GameBaccOddsBO gameBaccOdds) {
        //庄
        if (awardOption.equals(TgBaccRuleEnum.Z.getCode())) {
            return winBetZ(bet, gameBaccOdds);
        }
        //闲
        if (awardOption.equals(TgBaccRuleEnum.X.getCode())) {
            return winBetX(bet, gameBaccOdds);
        }
        //和
        if (awardOption.equals(TgBaccRuleEnum.H.getCode())) {
            return winBetH(bet, gameBaccOdds);
        }
        //庄对
        if (awardOption.equals(TgBaccRuleEnum.ZD.getCode())) {
            return winBetZd(bet, gameBaccOdds);
        }
        //闲对
        if (awardOption.equals(TgBaccRuleEnum.XD.getCode())) {
            return winBetXd(bet, gameBaccOdds);
        }
        //幸运六就是庄赢
        //幸运六(ss2)
        if (awardOption.equals(TgBaccRuleEnum.SS2.getCode())) {
            return winBetSs2(bet, gameBaccOdds);
        }
        if (awardOption.equals(TgBaccRuleEnum.SS3.getCode())) {
            return winBetSs3(bet, gameBaccOdds);
        }
        return null;
    }

    private static BetAwardBO winBetSs3(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //幸运六(ss3)-幸运六(三张牌)
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountSs() > 0) {
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountSs());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getSs3();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(幸运六(三张牌)-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        } else {
            //幸运六就是庄赢 按照庄赢
            //如果中了幸运6 还要检查 庄是否有下注
            if (bet.getAmountZ() > 0) {
                betAmount = BigDecimal.valueOf(bet.getAmountZ());
                //获取中奖选择的下注赔率
                BigDecimal odds = gameBaccOdds.getZ();
                //赢的钱
                BigDecimal winAmount = odds.multiply(betAmount);
                //该派彩的钱
                finalAmount = betAmount.add(winAmount);
                bo.setRemark("(幸运六(三张牌)-庄-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
            }
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }

    private static BetAwardBO winBetSs2(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //幸运六(ss2)-幸运六(两张牌)
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountSs() > 0) {
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountSs());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getSs2();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(幸运六(两张牌)-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        } else {
            //幸运六就是庄赢 按照庄赢
            //如果中了幸运6 还要检查 庄是否有下注
            if (bet.getAmountZ() > 0) {
                betAmount = BigDecimal.valueOf(bet.getAmountZ());
                //获取中奖选择的下注赔率
                BigDecimal odds = gameBaccOdds.getZ();
                //赢的钱
                BigDecimal winAmount = odds.multiply(betAmount);
                //该派彩的钱
                finalAmount = betAmount.add(winAmount);
                bo.setRemark("(幸运六(两张牌)-庄-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
            }
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }

    private static BetAwardBO winBetXd(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //闲对
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountXd() > 0) {
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountXd());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getXd();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(闲对-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }

    private static BetAwardBO winBetZd(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //庄对
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountZd() > 0) {
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountZd());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getZd();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(庄对-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }

    private static BetAwardBO winBetH(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //和
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountH() > 0) {
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountH());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getH();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(和-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        } else {
            //开 和 时 检查  庄闲 是否投注
            //1 ：1 (开和局时退回下注金额)
            if (bet.getAmountX() > 0 || bet.getAmountZ() > 0) {
                betAmount = bet.getAmountX() > 0 ? BigDecimal.valueOf(bet.getAmountX()) : BigDecimal.valueOf(bet.getAmountZ());
                finalAmount = betAmount;
                bo.setRemark("(和-开和局时退回下注金额 :" + betAmount + ")");
            }
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }

    private static BetAwardBO winBetX(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //闲
        //获取下注金额
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        if (bet.getAmountX() > 0) {
            //中
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountX());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getX();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(闲-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }


    private static BetAwardBO winBetZ(Bet bet, GameBaccOddsBO gameBaccOdds) {
        //庄
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        BetAwardBO bo = new BetAwardBO();
        //判断是否中奖
        if (bet.getAmountZ() > 0) {
            //中
            //获取下注金额
            betAmount = BigDecimal.valueOf(bet.getAmountZ());
            //获取中奖选择的下注赔率
            BigDecimal odds = gameBaccOdds.getZ();
            //赢的钱
            BigDecimal winAmount = odds.multiply(betAmount);
            //该派彩的钱
            finalAmount = betAmount.add(winAmount);
            bo.setRemark("(庄-中奖 下注金额:" + betAmount + "赔率:" + odds + "派彩:" + finalAmount + ")");
        }
        bo.setBetAmount(betAmount);
        bo.setFinalAmount(finalAmount);
        return bo;
    }


    /**
     * 获取中奖选择的下注赔率
     *
     * @param gameBaccOdds
     * @param bet
     * @param awardOption
     * @return
     *//*
    private BigDecimal getOdds(GameBaccOddsBO gameBaccOdds, Bet bet, String awardOption) {
        if (awardOption.equals(TgBaccRuleEnum.Z.getCode())) {
            if (bet.getAmountZ() > 0) {
                return gameBaccOdds.getZ();
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.X.getCode())) {
            if (bet.getAmountX() > 0) {
                return gameBaccOdds.getX();
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.H.getCode())) {
            if (bet.getAmountH() > 0) {
                return gameBaccOdds.getH();
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.ZD.getCode())) {
            if (bet.getAmountZd() > 0) {
                return gameBaccOdds.getZd();
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.XD.getCode())) {
            if (bet.getAmountXd() > 0) {
                return gameBaccOdds.getXd();
            }
        }
        //幸运6 庄也中奖
        if (awardOption.equals(TgBaccRuleEnum.SS2.getCode())) {
            if (bet.getAmountSs() > 0) {
                return gameBaccOdds.getSs2();
            }
            if (bet.getAmountZ() > 0) {
                return gameBaccOdds.getZ();
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.SS3.getCode())) {
            if (bet.getAmountSs() > 0) {
                return gameBaccOdds.getSs3();
            }
            if (bet.getAmountZ() > 0) {
                return gameBaccOdds.getZ();
            }
        }
        return BigDecimal.ZERO;
    }

    *//**
     * 注单 是否中奖
     *
     * @param bet
     * @param awardOption
     * @return
     *//*
    private boolean isWinBet(Bet bet, String awardOption) {
        if (awardOption.equals(TgBaccRuleEnum.Z.getCode())) {
            if (bet.getAmountZ() > 0) {
                return true;
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.X.getCode())) {
            if (bet.getAmountX() > 0) {
                return true;
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.H.getCode())) {
            if (bet.getAmountH() > 0) {
                return true;
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.ZD.getCode())) {
            if (bet.getAmountZd() > 0) {
                return true;
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.XD.getCode())) {
            if (bet.getAmountXd() > 0) {
                return true;
            }
        }
        //幸运6 庄也中奖
        if (awardOption.equals(TgBaccRuleEnum.SS2.getCode())) {
            if (bet.getAmountSs() > 0) {
                return true;
            }
            if (bet.getAmountZ() > 0) {
                return true;
            }
        }
        if (awardOption.equals(TgBaccRuleEnum.SS3.getCode())) {
            if (bet.getAmountSs() > 0) {
                return true;
            }
            if (bet.getAmountZ() > 0) {
                return true;
            }
        }
        return false;
    }*/


}
