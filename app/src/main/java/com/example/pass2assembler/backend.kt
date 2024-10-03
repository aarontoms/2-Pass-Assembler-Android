package com.example.pass2assembler
import android.util.Log

data class Pass1Result(val intermediate: String, val symtab: String)
data class Pass2Result(val output: String, val output2: String)

class Backend {
    companion object{
        fun pass1(input: String, optab: String): Pass1Result{
            val inputArr = input.split('\n').map { line ->
                line.trim().split(Regex("\\s+"))

            }
            val optabArr = optab.split('\n').map { line ->
                line.trim().split(Regex("\\s+"))
            }

            val interAddr = mutableListOf<String>()
            val symtabArr = mutableListOf<MutableList<Any>>()
            var locctr = 0
            var prev = 0
            var i = 0
            val intermediate = StringBuilder()
            val symtab = StringBuilder()
            println("yooooo")
            if(inputArr[0][1] == "START"){
                locctr = inputArr[0][2].toInt(16)
                prev = locctr
                interAddr.add(locctr.toString(16))
//                println("First interAddr: $interAddr")
            }
            while(inputArr[i][1] != "END"){
                i++
                var found = false
                val opcode = inputArr[i][1]

                for (x in optabArr.indices) {
                    if (optabArr[x][0] == opcode) {
//                        println("found $opcode")
                        locctr += 3
                        found = true
                        break
                    }
                }

                if (!found) {
                    when (inputArr[i][1]) {
                        "WORD" -> locctr += 3
                        "RESW" -> locctr += 3 * inputArr[i][2].toInt()
                        "RESB" -> locctr += inputArr[i][2].toInt()
                        "BYTE" -> {
                            val len = inputArr[i][2].length
                            locctr += len - 3
                        }
                        else -> {
                            println("Invalid opcode ${inputArr[i][1]}")
                        }
                    }
                }

                interAddr.add(prev.toString(16))
                if (inputArr[i][0] != "-") {
                    var flag = 0
                    for (x in symtabArr.indices) {
                        if (symtabArr[x][0] == inputArr[i][0]) {
                            flag = 1
                            symtabArr[x][2] = 1
                        }
                    }
                    symtabArr.add(mutableListOf(inputArr[i][0], prev.toString(16), flag))
                }
                prev = locctr

            }

            intermediate.append("-\t\t\t${inputArr[0][0]}\t\t\t${inputArr[0][1]}\t\t\t${inputArr[0][2]}\n")
            for (j in 1 until interAddr.size) {
                intermediate.append("${interAddr[j]}\t\t\t${inputArr[j][0]}\t\t\t${inputArr[j][1]}\t\t\t${inputArr[j][2]}\n")
            }

            if (intermediate.isNotEmpty()){
                intermediate.deleteCharAt(intermediate.length - 1)
            }

            for (j in symtabArr.indices) {
                symtab.append("${symtabArr[j][0]}\t\t\t${symtabArr[j][1]}\t\t\t${symtabArr[j][2]}\n")
            }
            return Pass1Result(intermediate.toString(), symtab.toString())
        }

        fun pass2(optab: String, intermediate: String, symtab: String): Pass2Result {
            val optabArr = optab.split('\n').map { line ->
                line.trim().split(Regex("\\s+"))
            }
            val intermediateArr = intermediate.split('\n').map { line ->
                line.trim().split(Regex("\\s+"))
            }
            val symtabArr = symtab.split('\n').map { line ->
                line.trim().split(Regex("\\s+"))
            }

            var i = 0
            val objectCodeArr = mutableListOf<String>()
            val output = StringBuilder()
            val output2 = StringBuilder()
            while (intermediateArr[i][2] != "END") {
                var found = false
                for (opLine in optabArr) {
                    if (opLine[0] == intermediateArr[i][2]) {
                        found = true
//                        Log.d("opLine[0]", opLine[0])
                        if(intermediateArr[i][3] == "-"){
                            objectCodeArr.add("${opLine[1]}0000")
                            break
                        }
                        var objectCode = opLine[1]
                        for (symLine in symtabArr) {
                            if (symLine[0] == intermediateArr[i][3]) {
                                objectCode += symLine[1]
                                objectCodeArr.add(objectCode)
                            }
                        }
                    }
                }

                if (!found) {
                    when (intermediateArr[i][2]) {
                        "WORD" -> {
                            val value = intermediateArr[i][3].toInt()
                            val objectCode = value.toString(16).padStart(6, '0')
                            objectCodeArr.add(objectCode)
                        }
                        "BYTE" -> {
                            val value = intermediateArr[i][3].substring(2, intermediateArr[i][3].length - 1)
                            var objectCode = ""
                            for (char in value) {
                                objectCode += char.code.toString(16)
                            }
                            objectCodeArr.add(objectCode)
                        }
                        "RESW", "RESB" -> {
                            objectCodeArr.add("\t")
                        }
                    }
                }
                i++
            }
            objectCodeArr.add("\t")
//            println("Intermediate: $intermediateArr")
//            println("Object code: $objectCodeArr")
            output.append("${intermediateArr[0][0]}\t\t\t${intermediateArr[0][1]}\t\t\t${intermediateArr[0][2]}\t\t\t${intermediateArr[0][3]}\n")
            for (j in 1 until intermediateArr.size) {
                output.append("${intermediateArr[j][0]}\t\t\t${intermediateArr[j][1]}\t\t\t${intermediateArr[j][2]}\t\t\t${intermediateArr[j][3]}\t\t\t${objectCodeArr[j - 1]}\n")
            }

            val lower = intermediateArr[1][0].toInt(16)
            val upper = intermediateArr[intermediateArr.size - 1][0].toInt(16)
            val length = upper - lower
            output2.append("H^${intermediateArr[0][1].padEnd(6, '_')}^${intermediateArr[1][0]}^${length.toString(16).padStart(6, '0')}\n\n")
            var x = 1
            var text = StringBuilder()
            var size = 0
            var keri = false
            var start = intermediateArr[x][0]

            while (x < intermediateArr.size) {
                keri = false
                if (objectCodeArr[x - 1] == "\t") {
                    x++
                    continue
                }
                text.append("^").append(objectCodeArr[x - 1])
                size += objectCodeArr[x - 1].length / 2
                if (size > 21) {
                    keri = true
                    size -= objectCodeArr[x - 1].length / 2
                    text.setLength(text.length - objectCodeArr[x - 1].length - 1)
                    output2.append("T^").append(start).append("^")
                        .append(size.toString(16).padStart(2, '0'))
                        .append(text).append("\n")
                    start = intermediateArr[x][0]
                    text.setLength(0)
                    size = 0
                    continue
                }
                x++
            }
            if (!keri) {
                output2.append("T^").append(start).append("^")
                    .append(size.toString(16).padStart(2, '0'))
                    .append(text).append("\n\n")
            }

            output2.append("E^${intermediateArr[1][0]}")

            return Pass2Result(output.toString(), output2.toString())
        }
    }
}