package com.example.pass2assembler

import android.app.Activity
import android.util.Log
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pass2assembler.DarkColors

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val FILE_SELECT_CODE = 1000

class MainActivity : ComponentActivity() {
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var topSelected by mutableStateOf("")
        var topTextFieldContent by mutableStateOf("")

        var bottomSelected by mutableStateOf("")
        var bottomTextFieldContent by mutableStateOf("")


        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileContent = fileReader(this, uri)
                    fileWriter(this, "$topSelected.txt", fileContent)
                    topTextFieldContent = fileContent
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(DarkColors.background)
                    .verticalScroll(scrollState)
            ){
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Header()
                    Spacer(modifier = Modifier.height(24.dp))
                    Top(getContent = getContent,
                        modifier = Modifier,
                        topSelected = topSelected,
                        onTopSelectedChange = { newSelection -> topSelected = newSelection },
                        textFieldContent = topTextFieldContent,
                        onTextFieldContentChange = { newContent -> topTextFieldContent = newContent }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Bottom(
                        modifier = Modifier,
                        bottomSelected = bottomSelected,
                        onBottomSelectedChange = { newSelection -> bottomSelected = newSelection },
                        textFieldContent = bottomTextFieldContent,
                        onTextFieldContentChange = { newContent -> bottomTextFieldContent = newContent }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkColors.primary
    ) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
//            .padding(top = 24.dp)
            .background(backgroundColor)
            .systemBarsPadding(),
    ) {
        Column {
            Text(
                text = "Two Pass Assembler",
                style = TextStyle(
                    fontSize = 40.sp,
                    color = Color(0xFF62C062),
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aaron",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFFdcdcdc),
                    ),
                    textAlign = TextAlign.Right,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.github_logo),
                    contentDescription = "GitHub Logo",
                    modifier = Modifier.size(16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/aarontoms"))
                        context.startActivity(intent)
                    },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "github.com/aarontoms",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFFdcdcdc),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top(
    getContent: ActivityResultLauncher<Intent>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkColors.primary,
    topSelected: String,
    onTopSelectedChange: (String) -> Unit,
    textFieldContent: String,
    onTextFieldContentChange: (String) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "input.txt").toUri())
        onTextFieldContentChange(fieldContent)
        onTopSelectedChange("input")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(backgroundColor)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ){
                        Btn("Input", modifier = Modifier){
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "input.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onTopSelectedChange("input")
//                        Log.d("FileReader", "Input Data: $textFieldContent")
                        }
                        Btn("Optab", modifier = Modifier){
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "optab.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onTopSelectedChange("optab")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ){
                        Text(
                            text = when (topSelected) {
                                "input" -> "Input File"
                                "optab" -> "Optab File"
                                else -> "Select a file"
                            },
                            color = DarkColors.onPrimary,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(top = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ){
                            Button(
                                onClick = {
                                    showDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkColors.primary,
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ){
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_file_upload_24),
                                    contentDescription = "Upload",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    Log.d("Button", "SaveButton clicked")
//                            Log.d("SaveToFile", "File saved successfully: $topSelected")
                                    val finalFileName = "$topSelected.txt"
                                    fileWriter(context, finalFileName, textFieldContent)
                                },
                                contentPadding = PaddingValues(8.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF23863c),
                                    contentColor = DarkColors.onSecondary
                                )
                            ) {
                                Text(text = "Save")
                            }
                        }
                        if (showDialog) {
                            UploadDialog(
                                onDismiss = { showDialog = false },
                                onFileSelected = { selected ->
//                                    val fileName = "$selected.txt"
//                                    val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), fileName).toUri())
//                                    onTextFieldContentChange(fieldContent)
//                                    Log.d("Data", "Data: $fieldContent")
                                    onTopSelectedChange(selected)

                                    showDialog = false
                                },
                                getContent = getContent
                            )
                        }
                    }

                    Field(text = textFieldContent,
                        onTextChange = { newText -> onTextFieldContentChange(newText) },
                        position = "top"
                    )
                }
            }
            Box(
                modifier = Modifier
//                    .background(DarkColors.secondary)
                    .align(Alignment.CenterHorizontally),
            ) {
                val input = fileReader(context, File(context.getExternalFilesDir(null), "input.txt").toUri())
                val optab = fileReader(context, File(context.getExternalFilesDir(null), "optab.txt").toUri())
                RunBtn(input, optab)
            }
        }
    }
}

@Composable
fun Bottom(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkColors.primary,
    bottomSelected: String,
    onBottomSelectedChange: (String) -> Unit,
    textFieldContent: String,
    onTextFieldContentChange: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "intermediate.txt").toUri())
        onTextFieldContentChange(fieldContent)
        onBottomSelectedChange("intermediate")
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(backgroundColor)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp)
                    ){
                        Btn("Intermediate", modifier = Modifier) {
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "intermediate.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onBottomSelectedChange("intermediate")
                        }
                        Btn("Symtab", modifier = Modifier) {
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "symtab.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onBottomSelectedChange("symtab")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    ){
                        Btn("Output File", modifier = Modifier) {
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "output.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onBottomSelectedChange("output")
                        }
                        Btn("Output", modifier = Modifier) {
                            val fieldContent = fileReader(context, File(context.getExternalFilesDir(null), "output2.txt").toUri())
                            onTextFieldContentChange(fieldContent)
                            onBottomSelectedChange("output2")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ){
                        Text(
                            text = when (bottomSelected) {
                                "intermediate" -> "Intermediate File"
                                "symtab" -> "Symtab File"
                                "output" -> "Output File"
                                "output2" -> "Output"
                                else -> "Select a file"
                            },
                            color = DarkColors.onPrimary,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(top = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End

                        ){
                            Button(
                                modifier = Modifier.padding(end = 8.dp),
                                onClick = {
                                    Log.d("Button", "ClearButton clicked")
                                    fileWriter(context, "input.txt", "")
                                    fileWriter(context, "optab.txt", "")
                                    fileWriter(context, "intermediate.txt", "")
                                    fileWriter(context, "symtab.txt", "")
                                    fileWriter(context, "output.txt", "")
                                    fileWriter(context, "output2.txt", "")
                                },
                                contentPadding = PaddingValues(8.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF853828),
                                    contentColor = DarkColors.onSecondary
                                )
                            ) {
                                Text(
                                    text = "Clear",
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                    Field(text = textFieldContent,
                        onTextChange = {  },
                        readOnly = true,
                        position = "bottom"
                    )
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Field(text: String, onTextChange: (String) -> Unit, readOnly: Boolean = false, position: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ){
        TextField(
            value = text,
            textStyle = TextStyle(
                color = DarkColors.onSecondary,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            ),
            onValueChange = onTextChange,
            placeholder = { Text(
                if (position == "top") "Enter input here..."
                else "Run the assembler to see output here...",
            ) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .padding(bottom = 16.dp)
                .height(250.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = DarkColors.secondary,
                focusedTextColor = DarkColors.onSecondary,
                unfocusedTextColor = DarkColors.onSecondary,
                focusedPlaceholderColor = DarkColors.onTertiary,
                unfocusedPlaceholderColor = DarkColors.onTertiary
            ),
            readOnly = readOnly
        )
    }
}

@Composable
fun RunBtn(input: String, optab: String) {
    val context = LocalContext.current
    Button(
        onClick = {
            Log.d("Button", "RunButton clicked")
            val (intermediateOutput, symtabOutput) = Backend.pass1(input, optab)
            fileWriter(context, "intermediate.txt", intermediateOutput)
            fileWriter(context, "symtab.txt", symtabOutput)

            val (output, output2) = Backend.pass2(optab, intermediateOutput, symtabOutput)
            fileWriter(context, "output.txt", output)
            fileWriter(context, "output2.txt", output2)
        },
        modifier = Modifier.padding(32.dp),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF23863c),
            contentColor = DarkColors.onSecondary
        )
    ) {
        Text(
            text = "Run",
            style = TextStyle(
                fontSize = 20.sp
            )
        )
    }
}

@Composable
fun Btn(text: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkColors.secondary,
            contentColor = DarkColors.onSecondary
        ),
        contentPadding = PaddingValues(10.dp),
    ) {
        Text(text = text)
    }
}

@Composable
fun UploadDialog(onDismiss: () -> Unit, onFileSelected: (String) -> Unit, getContent: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf("input") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            color = DarkColors.surface,
            contentColor = DarkColors.onSurface,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Upload File: $selected",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                            }
//                            (context as? Activity)?.startActivityForResult(intent, FILE_SELECT_CODE)
                            getContent.launch(intent)
                            onFileSelected(selected)
                            onDismiss()
                        },
                        modifier = Modifier
                            .padding(end = 8.dp),
                        contentPadding = PaddingValues(start = 6.dp, end = 6.dp),
                    ) {
                        Text("Choose File")
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            selected = "input"
                        },
                        modifier = Modifier
                            .padding(end = 8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == "input") Color(0xFF2B324B) else DarkColors.secondary,
                            contentColor = DarkColors.onSecondary
                        )
                    ) {
                        Text("Input")
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            selected = "optab"
                        },
                        modifier = Modifier
                            .padding(end = 8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == "optab") Color(0xFF2B324B) else DarkColors.secondary,
                            contentColor = DarkColors.onSecondary
                        )
                    ) {
                        Text(text = "Optab")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(0.3f),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun fileReader(context: Context, uri: Uri): String{
    return try {
        val contentResolver = context.contentResolver
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            inputStream.bufferedReader().use { it.readText() }
        } else {
            val outputStream = contentResolver.openOutputStream(uri)
            outputStream.use {
                it?.write("".toByteArray())
            }
            ""
        }
    } catch (e: Exception) {
        Log.e("FileReader", "Error reading file: inputStream is null")
        val contentResolver = context.contentResolver
        Log.e("FileReader", "Error reading file: ${e.message}")
        val outputStream = contentResolver.openOutputStream(uri)
        outputStream.use {
            it?.write("".toByteArray())
        }
        ""
    }
}

fun fileWriter(context: Context, fileName: String, text: String){
//    Log.d("SaveTrying", "File saving: $text")
    try {
        val file = File(context.getExternalFilesDir(null), fileName)
        val fileOutputStream: FileOutputStream = FileOutputStream(file)
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
        Log.d("SaveToFile", "File saved successfully to: ${file.absolutePath}")
    } catch (e: Exception) {
        Log.e("SaveToFile", "Error saving file: ${e.message}")
    }
}