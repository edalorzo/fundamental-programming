Fundamental Programming Principles
==================================

This project was intended as an exploration and demonstration of fundamental programming principles in the following areas:

- Input Validation
- Proper use of exceptions
- Fundamental Logging

Defensive Programming
---------------------

A good place to start is in Chapter 8 of the book `Code Complete`_, under Defensive Programming:

  In school you might have heard the expression, "Garbage in, garbage out." That expression is essentially software development's version of caveat emptor: let the user beware.

  For production software, garbage in, garbage out isn't good enough. A good program never puts out garbage, regardless of what it takes in. A good program uses "garbage in, nothing out," "garbage in, error message out," or "no garbage allowed in" instead.

  By today's standards, "garbage in, garbage out" is the mark of a sloppy, nonsecure program.

Robustness vs Correctness
-------------------------

There are two major principles that we must balance when designing and implementing defensive code. The following is another quote from the same book:

  Developers tend to use these terms informally, but, strictly speaking, these terms are at opposite ends of the scale from each other. *Correctness* means never returning an inaccurate result; returning no result is better than returning an inaccurate result. *Robustness* means always trying to do something that will allow the software to keep operating, even if that leads to results that are inaccurate sometimes.

  Safety-critical applications tend to favor correctness to robustness. It is better to return no result than to return a wrong result. The radiation machine is a good example of this principle.

  Consumer applications tend to favor robustness to correctness. Any result whatsoever is usually better than the software shutting down. The word processor I'm using occasionally displays a fraction of a line of text at the bottom of the screen. If it detects that condition, do I want the word processor to shut down? No. I know that the next time I hit Page Up or Page Down, the screen will refresh and the display will be back to normal.

Balancing these two principles is vital while coding functions and methods.

Design By Contract
------------------

Functions or methods are the fundamental building blocks of any software application and we should strive to `design them by contract <https://www.cs.umd.edu/class/fall2002/cmsc214/Projects/P1/proj1.contract.html>`_. This implies that a method must guarantee all its preconditions. During the execution of the code must ensure its invariants are hold and finally, when finished, it must guarantee a number of postconditions. Failure to do any of this should be signaled immediately.

* **Precondition**:  a condition that must be true of the parameters of a method and/or data members, if the method is to behave correctly, prior to running the code in the method.
* **Postcondition**: a condition that is true after running the code in a method.
* **Class Invariant**: a condition that is true before and after running the code in a method (except constructors and destructors).

Therefore a method contract states what input the method receives, and establishes a set of constraints or preconditions that such input must satisfy. The method contract also states what the method does, in other words what task is logically performed by the method and what results can be expected, what side effects should have happened, etc. There is also a number of postconditions that a method contract must guarantee upon successful completion of the method, in other words, guaranteed expectations about the final state of the world after the method is done running.

Any deviation of the contract must throw an exception or signal the problem somehow, which is the way to make obvious that either the preconditions were not satisfied, or the postconditions could not be met for any reason.

Failure to validate both the preconditions and postconditions of a method contract can leave a system in an inconsistent state in which the program results can no longer be guaranteed and where errors can happen unexpectedly due to the inconsistent state of the data.

We typically document the contract in the interface method documentation. For example, consider the following example from a ``BankAccount`` interface (intentionally oversimplified to make examples simpler to understand):

.. code-block:: java

 public interface BankAccount {

    /**
     * Withdrawing money from a savings account reduces its balance by the
     * provided withdrawal amount.
     *
     * For the withdrawal operation to succeed, the savings account is expected to have enough balance
     * to satisfy the withdrawal.
     *
     * At any point in time the final balance of the saving accounts may
     * never be smaller than 0.
     *
     * @param amount - the amount you want to withdraw from your account.
     * @return the balance in the account after the withdrawal.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     * @throws InsufficientFundsException if the current {@code balance} is smaller than {@code amount}
     */
    double withdrawMoney(double amount);

    /**
     * Saving money into the savings account increases its balance by the saved amount.
     *
     * In order that the saving succeed, the final account balance must represent a positive amount of money
     *
     * At any point in time the final balance of the saving accounts may never be smaller than 0.
     *
     * @param amount - the amount to save into the account.
     * @return the balance of the account after savings.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */
    double saveMoney(double amount);
 }

The implementation class of this interface then must satisfy everything stated in the contract of its methods and our test classes must strive to satisfy those contracts.

Consider another example: let's say you defining  a ``Fraction`` class to represent that mathematical concept. You may need to follow a contract with the following rules:

* **Precondition**: the denominator must never be ``0``.
* **Invariant**: fractions will be kept in reduced form (i.e. ``2/3`` instead of ``6/9``, ``6`` instead of ``6/1``, ``0`` instead of ``0/2``)
* **Postcondition**: a fraction with a denominator of ``1`` will be represented as a whole number, not as a fraction (i.e. ``2`` instead of ``2/1``).
* **Postcondition**: a numerator of 0 will be represented as the whole number ``0``, not as a fraction (i.e. ``0`` instead of ``0/2``).

The **principle here** is that you may want to do the effort of documenting your interface contracts such that developers creating implementation make sure the contract holds at all times in their implementation and in their unit tests.

Once you have a contract properly defined you can **write tests to verify your contracts**:

.. code-block:: java

 public class SavingsAccountTest {

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");
    private final BankAccount bankAccount = new SavingsAccount(accountNumber);

    @Test
    public void saveMoney() {
        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithNegativeAmount() {
        bankAccount.saveMoney(-100);
        Assert.fail("Savings of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithZeroAmount() {
        bankAccount.saveMoney(0.0);
        Assert.fail("Savings of $0 should fail!");
    }

    @Test
    public void withdrawMoney() {
        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);

        balance = bankAccount.withdrawMoney(50);
        assertThat(balance).isEqualTo(50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithNegativeAmount() {
        bankAccount.withdrawMoney(-100);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithZeroAmount() {
        bankAccount.withdrawMoney(0.0);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = InsufficientFundsException.class)
    public void withdrawMoneyWithInsufficientFunds() {
        bankAccount.withdrawMoney(50);
        Assert.fail("Withdrawal should fail when there aren't sufficient funds!");
    }
 }

If you're following TDD style, you need not have implemented the ``SavingsAccount`` class and initially all tests would fail and gradually start passing as the methods are implemented properly one by one in the class.

Further Reading
---------------

* `Design By Contract`_
* `Null References: The Billion Dollar Mistake <https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare>`_
* `Code Complete`_
* `Effective Java`_

.. _Code Complete: https://www.amazon.com/Code-Complete-Practical-Handbook-Construction/dp/0735619670
.. _Effective Java: https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997/
.. _Design By Contract: https://www.cs.umd.edu/class/fall2002/cmsc214/Projects/P1/proj1.contract.html