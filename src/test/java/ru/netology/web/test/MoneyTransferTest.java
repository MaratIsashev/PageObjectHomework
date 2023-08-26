package ru.netology.web.test;


import org.junit.jupiter.api.*;

import ru.netology.web.page.DashboardPage;

import ru.netology.web.page.LoginPageV2;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {
  LoginPageV2 loginPageV2;
  DashboardPage dashboardPage;
  @BeforeEach
  void setup(){
    loginPageV2 = open("http://localhost:9999/", LoginPageV2.class);
    var authInfo = getAuthInfo();
    var verificationPage = loginPageV2.validLogin(authInfo);
    var verificationCode = getVerificationCode();
    dashboardPage = verificationPage.validVerify(verificationCode);
  }
  @Test
  void shouldTransferFrom1To2(){
    var firstCardInfo = getFirstNumber();
    var secondCardInfo = getSecondNumber();
    var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
    var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    var amount = generateValidAmount(firstCardBalance);
    var expectedBalanceFirstCard = firstCardBalance - amount;
    var expectedBalanceSecondCard = secondCardBalance + amount;
    var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
    dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
    var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
    var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
    assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
    assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
  }
  @Test
  void shouldErrorIfAmountMoreThanBalance(){
    var firstCardInfo = getFirstNumber();
    var secondCardInfo = getSecondNumber();
    var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
    var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    var amount = generateInvalidAmount(secondCardBalance);
    var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
    transferPage.makeTransfer(String.valueOf(amount), secondCardInfo );
    transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающая остаток на карте списания");
    var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
    var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
    assertEquals(firstCardBalance, actualBalanceFirstCard);
    assertEquals(secondCardBalance, actualBalanceSecondCard);

  }


}