    package mars.mips.instructions.customlangs;
    import mars.simulator.*;
    import mars.mips.hardware.*;
    import mars.mips.instructions.syscalls.*;
    import mars.*;
    import mars.util.*;
    import java.util.*;
    import java.io.*;
    import mars.mips.instructions.*;
    import java.util.Random;



public class CasinoLanguage extends CustomAssembly {
    @Override
    public String getName(){
        return "Casino Assembly";
    }

    @Override
    public String getDescription(){
        return "Assembly language to let you win big";
    }

    @Override
    protected void populate(){
        instructionList.add(
            new BasicInstruction("deposit $t1, -100",
                "Deposit : Change the account value of $t1 by an immediate value",
                BasicInstructionFormat.I_FORMAT,
                "001000 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[0]);
                        int add2 = operands[1] << 16 >> 16;
                        int sum = add1 + add2;
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("compound $t1, $t2, $t3",
                "Compound : Adds account values of register $t2 and $t3 into $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100000",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = RegisterFile.getValue(operands[2]);
                        int sum = add1 + add2;
                        RegisterFile.updateRegister(operands[0],sum);
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("withdraw $t1, $t2, $t3",
                "Withdraw: Subtracts account values of $t3 from $t2 into $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 010000",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int sub1 = RegisterFile.getValue(operands[1]);
                        int sub2 = RegisterFile.getValue(operands[2]);
                        int difference = sub1 - sub2;
                        RegisterFile.updateRegister(operands[0],difference);
                    }
                }
            )
        );
        
        instructionList.add(
            new BasicInstruction("tablechange label",
                "Tablechange: Jumps to a new table label",
                BasicInstructionFormat.J_FORMAT,
                "000110 ffffffffffffffffffffffffff",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        String label = statement.getOriginalTokenList().get(1).getValue();

                        int labelAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                        RegisterFile.setProgramCounter(labelAddress);
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("outcome $t1, $t2, $t3",
                "Outcome : multiplies account values of $t2 and $t3 into $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 011000",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int mul1 = RegisterFile.getValue(operands[1]);
                        int mul2 = RegisterFile.getValue(operands[2]);
                        int sum = mul1 * mul2;
                        RegisterFile.updateRegister(operands[0],sum);
                    }
                }
            )
        );
        // I could not for the life of me get low or high roller to work, but they were supposed to be like blt and bgt.
        // They were the only instructions I couldn't figure out :(
        instructionList.add(
            new BasicInstruction("lowroller $t0, $t1, label",
                "lowroller : branches to the label if $t1 is less than $t0",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "111001 sssss ttttt iiiiiiiiiiiiiiii",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int int1 = RegisterFile.getValue(operands[0]);
                        int int2 = RegisterFile.getValue(operands[1]);
                        int branchOffset = 0;

                        if (int2 < int1){
                             branchOffset = operands[2];
                        }

                        RegisterFile.setProgramCounter(RegisterFile.getProgramCounter() + (branchOffset << 2));
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("highroller $t0, $t1, label",
                "highroller : branches to the label if $t1 is greater than $t0",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "111111 sssss ttttt iiiiiiiiiiiiiiii",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int int1 = RegisterFile.getValue(operands[0]);
                        int int2 = RegisterFile.getValue(operands[1]);
                        int branchOffset = 0;

                        if (int2 > int1){
                            branchOffset = operands[2];
                        }

                        RegisterFile.setProgramCounter(RegisterFile.getProgramCounter() + (branchOffset << 2));
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("moveaccount $t1, $t2",
                "moveaccount : transfers account value to $t1, from $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss fffff 00000 00000 101111",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int transferValue = RegisterFile.getValue(operands[1]);
                        int tempValue = RegisterFile.getValue(operands[0]);

                        RegisterFile.updateRegister(operands[0],transferValue);
                        RegisterFile.updateRegister(operands[1],tempValue);
                        
                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("changegame $s1, -100",
                "changegame : takes an immediate value, uses $s1 to determine game to play",
                BasicInstructionFormat.I_FORMAT,
                "001101 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        
                        RegisterFile.updateRegister(operands[0],operands[1]);

                    }
                }
            )
        );
        instructionList.add(
            new BasicInstruction("loan $t1, $t2, $s5",
                "loan : loans money from a special banker account $s5 to player account $t1, by combining $s5 with $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss fffff ttttt 00000 011100",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        int banker = RegisterFile.getValue(operands[2]);
                        int playerOriginal = RegisterFile.getValue(operands[1]);

                        int sum = banker + playerOriginal;
                        RegisterFile.updateRegister(operands[0], sum);

                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("allin $t1",
                "allin : combines all player account values into $t1",
                BasicInstructionFormat.R_FORMAT,
                "001100 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        
                        int allInRegister = operands[0];
                        int allInValue = RegisterFile.getValue(operands[0]);
                        
                        for(int i = 8; i < 16; i++){    
                            if (i == allInRegister){
                                continue;
                            }
                            int money = RegisterFile.getValue(i);
                            allInValue += money;
                            RegisterFile.updateRegister(i,0);

                        }
                        RegisterFile.updateRegister(allInRegister, allInValue);
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("cointoss $t1",
                "cointoss : performs a cointoss random calculation, then updates $t1 accordingly",
                BasicInstructionFormat.R_FORMAT,
                "001110 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();

                        int value = RegisterFile.getValue(operands[0]);
                        Random random = new Random();
                        int toss = random.nextInt(2);

                        if (toss == 0){
                            value = 2 * value;
                        }
                        else if(toss == 1){
                            value = 0;
                        }
                        RegisterFile.updateRegister(operands[0],value);
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("poker $t1",
                "poker : creates a hand for player and dealer and checks their strength, then updates $t1 accordingly",
                BasicInstructionFormat.R_FORMAT,
                "001111 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();
                        
                        int value = RegisterFile.getValue(operands[0]);
                        Random random = new Random();
                        int dealer = random.nextInt(10);
                        int player = random.nextInt(10);

                        if (dealer == player){
                            value = value;
                        }
                        else if(dealer > player){
                            value = 0;
                        }
                        else if(dealer < player){
                            value = 2 * value;
                        }
                        RegisterFile.updateRegister(operands[0],value);

                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("blackjack $t1",
                "blackjack : creates a hand for dealer, creates a hand for player, checks if either is 21, otherwise higher hand wins",
                BasicInstructionFormat.R_FORMAT,
                "011111 fffff 00000 ssssssssssssssss",
                new SimulationCode(){
                    public void simulate(ProgramStatement statement) throws ProcessingException{
                        int[] operands = statement.getOperands();

                        int value = RegisterFile.getValue(operands[0]);
                        Random random = new Random();
                        int dealer = random.nextInt(21);
                        int player = random.nextInt(21);

                        if (dealer == 21){
                            value = 0;
                        }
                        else if (player == 21){
                            value =  value * 2;
                        }
                        else if (dealer > player){
                            value = 0;
                        }
                        else if (dealer < player){
                            value = value * 2;
                        }
                        RegisterFile.updateRegister(operands[0],value);
                        
                    }
                }
            )
        );

    }

}
