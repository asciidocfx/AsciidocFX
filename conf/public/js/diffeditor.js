window.aceDiff = new AceDiff({
    // ace: window.ace, // You Ace Editor instance
    element: '.acediff',
    left: {
        content: "",
    },
    right: {
        content: "",
    },
});

function updateContent(left, right) {
    let editors = window.aceDiff.getEditors();
    editors.left.setValue(left);
    editors.right.setValue(right);
    editors.left.clearSelection();
    editors.right.clearSelection();
}