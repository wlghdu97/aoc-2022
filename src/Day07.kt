sealed class Node {
    abstract val name: String
    abstract val parent: Directory?

    class Directory(
        override val name: String,
        override val parent: Directory?,
        val children: MutableList<Node> = mutableListOf()
    ) : Node() {
        val totalDirectorySize: Int
            get() = children.sumOf {
                when (it) {
                    is Directory -> it.totalDirectorySize
                    is File -> it.size
                }
            }

        fun totalDirectorySizeAtMost(limit: Int): Int {
            val rootTotal = totalDirectorySize
            val rootSum = if (rootTotal <= limit) rootTotal else 0
            return rootSum + children.filterIsInstance<Directory>().sumOf {
                it.totalDirectorySizeAtMost(limit)
            }
        }
    }

    class File(
        override val name: String,
        override val parent: Directory?,
        val size: Int
    ) : Node()
}

enum class Command(val actualName: String) {
    CD("cd"), LS("ls");
}

class Execution(
    val command: Command,
    val argument: String?,
    val results: List<String>
)

fun List<String>.toExecutions(): List<Execution> {
    val executions = arrayListOf<Execution>()

    class CommandArg(val command: Command, val arg: String?)

    val iterator = this.iterator()
    var currentCommand: CommandArg? = null
    val results = arrayListOf<String>()

    fun String.isCommandLine(): Boolean {
        return this.startsWith("$ ")
    }

    fun String.parseCommandLine(): CommandArg {
        val parts = this.split(" ")
        val command = when (parts[1]) {
            Command.CD.actualName -> Command.CD
            Command.LS.actualName -> Command.LS
            else -> throw IllegalArgumentException()
        }
        return CommandArg(command, parts.getOrNull(2))
    }

    fun flush() {
        val command = currentCommand ?: return
        executions.add(Execution(command.command, command.arg, results.toList()))
        currentCommand = null
        results.clear()
    }

    while (iterator.hasNext()) {
        val line = iterator.next()
        if (line.isCommandLine()) {
            flush()
            currentCommand = line.parseCommandLine()
        } else {
            results.add(line)
        }
    }
    flush()

    return executions
}

fun buildFileSystem(executions: List<Execution>): Node.Directory? {
    var root: Node.Directory? = null
    var head: Node.Directory? = null

    executions.forEach { exec ->
        when (exec.command) {
            Command.CD -> {
                when (exec.argument) {
                    "/" -> {
                        if (root == null) {
                            root = Node.Directory("/", null)
                        }
                        head = root
                    }
                    ".." -> {
                        head = head?.parent
                    }
                    else -> {
                        if (!exec.argument.isNullOrBlank()) {
                            val dirName = exec.argument
                            val targetDir = head?.children?.filterIsInstance<Node.Directory>()?.find { it.name == dirName }
                            if (targetDir != null) {
                                head = targetDir
                            }
                        }
                    }
                }
            }
            Command.LS -> {
                exec.results.forEach { result ->
                    val (first, second) = result.split(" ")
                    if (first == "dir") {
                        if (head?.children?.none { it is Node.Directory && it.name == second } == true) {
                            head?.children?.add(Node.Directory(second, head))
                        }
                    }
                    if (first.toIntOrNull() != null) {
                        val size = first.toInt()
                        if (head?.children?.none { it is Node.File && it.name == second } == true) {
                            head?.children?.add(Node.File(second, head, size))
                        }
                    }
                }
            }
        }
    }

    return root
}

fun List<String>.toFileSystem(): Node.Directory? {
    return buildFileSystem(this.toExecutions())
}

const val TOTAL_SIZE = 70_000_000
const val TARGET_FREE_SIZE = 30_000_000

fun Node.Directory.minimumTotalDirectorySizeMoreThan(limit: Int): Int {
    var min = Int.MAX_VALUE

    fun Node.Directory.visit(each: (Node) -> Unit) {
        each(this)
        this.children.filterIsInstance<Node.Directory>().forEach {
            it.visit(each)
        }
    }

    this.visit {
        if (it is Node.Directory) {
            val size = it.totalDirectorySize
            if (size in (limit + 1) until min) {
                min = size
            }
        }
    }

    return min
}

fun main() {
    fun part1(input: List<String>): Int {
        val root = input.toFileSystem() ?: throw RuntimeException()
        return root.totalDirectorySizeAtMost(100_000)
    }

    fun part2(input: List<String>): Int {
        val root = input.toFileSystem() ?: throw RuntimeException()
        val targetSize = TARGET_FREE_SIZE - (TOTAL_SIZE - root.totalDirectorySize)
        return root.minimumTotalDirectorySizeMoreThan(targetSize)
    }

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
